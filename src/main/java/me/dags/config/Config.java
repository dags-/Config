package me.dags.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class Config extends Node {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final Path path;

    private Config(ConfigurationLoader<CommentedConfigurationNode> loader, CommentedConfigurationNode root, Path path) {
        super(root);
        this.loader = loader;
        this.path = path;
    }

    /**
     * The configuration loader backing this config
     */
    public ConfigurationLoader<CommentedConfigurationNode> loader() {
        return loader;
    }

    /**
     * The path where this config is loaded from and saved to
     */
    public Path path() {
        return path;
    }

    /**
     * Load a new instance of the config from disk
     */
    public Config reload() {
        return must(path());
    }

    /**
     * Write the config to disk
     */
    public boolean save() {
        return write(loader(), backing(), path());
    }

    /**
     * Write the config to disk if it doesn't already exist
     */
    public boolean saveIfAbsent() {
        Path path = path();
        if (Files.exists(path)) {
            return true;
        }
        return save();
    }

    /**
     * Loads all configs in the given directory with the given extension
     */
    public static Stream<Config> all(Path dir, String extension) {
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*" + extension);
            return Files.list(dir).filter(path -> matcher.matches(path.getFileName())).map(Config::must);
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    /**
     * Maps all configs in the given directory with the given extension to a stream of the given type
     */
    public static <T> Stream<T> all(Path dir, String extension, Class<T> type) {
        return all(dir, extension).map(c -> c.bind(type, (T) null)).filter(Objects::nonNull);
    }

    /**
     * Loads or creates a new config from the given path.
     * Creates the parent directories and file if necessary.
     */
    public static Config must(Path path) {
        path = path.toAbsolutePath();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(DEFAULT_OPTIONS)
                .setSource(reader(path))
                .setSink(writer(path))
                .build();
        return must(loader, path);
    }

    /**
     * Loads or creates a new config from the given file path.
     * Creates the parent directories and file if necessary.
     */
    public static Config must(String file, String... path) {
        return must(Paths.get(file, path));
    }

    /**
     * Loads or creates a new config from the file in the given directory.
     * Creates the parent directories and file if necessary.
     */
    public static Config must(Path dir, String file) {
        return must(dir.resolve(file));
    }

    private static Callable<BufferedWriter> writer(Path path) {
        return () -> Files.newBufferedWriter(path, StandardCharsets.UTF_8);
    }

    private static Callable<BufferedReader> reader(Path path) {
        return () -> Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }

    private static Config must(ConfigurationLoader<CommentedConfigurationNode> loader, Path path) {
        CommentedConfigurationNode root = Config.read(loader);
        return new Config(loader, root, path);
    }

    private static CommentedConfigurationNode read(ConfigurationLoader<CommentedConfigurationNode> loader) {
        try {
            return loader.load();
        } catch (Throwable e) {
            return loader.createEmptyNode();
        }
    }

    private static boolean write(ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationNode node, Path path) {
        try {
            mkdirs(path.getParent());
            loader.save(node);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void mkdirs(Path dir) {
        if (dir == null) {
            return;
        }

        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
