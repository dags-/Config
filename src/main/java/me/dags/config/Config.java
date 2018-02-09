package me.dags.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public interface Config extends Node {

    ConfigurationOptions DEFAULT_OPTIONS = ConfigurationOptions.defaults().setShouldCopyDefaults(true);

    ConfigurationLoader<CommentedConfigurationNode> loader();

    Path path();

    default boolean save() {
        return ConfigNode.write(loader(), node(), path());
    }

    default Config reload() {
        return must(path());
    }

    static Config must(String path, String... children) {
        return must(Paths.get(path, children));
    }

    static Config must(ConfigurationLoader<CommentedConfigurationNode> loader, String path, String... children) {
        return must(loader, Paths.get(path, children));
    }

    static Config must(Path path) {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(DEFAULT_OPTIONS)
                .setPath(path)
                .build();
        return must(loader, path);
    }

    static Config must(ConfigurationLoader<CommentedConfigurationNode> loader, Path path) {
        CommentedConfigurationNode root = ConfigNode.read(loader);
        return new ConfigNode(loader, root, path);
    }

    static Stream<Config> all(Path dir, String extension) {
        try {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*" + extension);
            return Files.list(dir).filter(path -> matcher.matches(path.getFileName())).map(Config::must);
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    static <T> Optional<T> must(Path path, Class<T> type) {
        T t = must(path).bind(type, (T) null);
        return Optional.ofNullable(t);
    }

    static <T> Stream<T> all(Path dir, String extension, Class<T> type) {
        return all(dir, extension).map(c -> c.bind(type, (T) null)).filter(Objects::nonNull);
    }
}
