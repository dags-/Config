package me.dags.config;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author dags <dags@dags.me>
 */
public interface Config extends Node {

    ConfigurationOptions DEFAULT_OPTIONS = ConfigurationOptions.defaults().setShouldCopyDefaults(true);

    ConfigurationLoader<CommentedConfigurationNode> loader();

    Path path();

    default void save() {
        ConfigNode.write(loader(), node(), path());
    }

    default Config reload() {
        return must(path());
    }

    static Config must(String root, String... path) {
        return must(Paths.get(root, path));
    }

    static Config must(Path path) {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(DEFAULT_OPTIONS)
                .setPath(path.toAbsolutePath())
                .build();

        CommentedConfigurationNode root = ConfigNode.read(loader);

        return new ConfigNode(loader, root, path.toAbsolutePath());
    }
}
