package me.dags.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author dags <dags@dags.me>
 */
class ConfigNode extends BasicNode implements Config {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final Path path;

    ConfigNode(ConfigurationLoader<CommentedConfigurationNode> loader, CommentedConfigurationNode root, Path path) {
        super(root);
        this.loader = loader;
        this.path = path;
    }

    @Override
    public ConfigurationLoader<CommentedConfigurationNode> loader() {
        return loader;
    }

    @Override
    public Path path() {
        return path;
    }

    static CommentedConfigurationNode read(ConfigurationLoader<CommentedConfigurationNode> loader) {
        try {
            return loader.load();
        } catch (Throwable e) {
            return loader.createEmptyNode();
        }
    }

    static boolean write(ConfigurationLoader<CommentedConfigurationNode> loader, ConfigurationNode node, Path path) {
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
