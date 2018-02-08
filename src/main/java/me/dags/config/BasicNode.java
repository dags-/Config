package me.dags.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * @author dags <dags@dags.me>
 */
class BasicNode implements Node {

    private final CommentedConfigurationNode node;

    BasicNode(CommentedConfigurationNode node) {
        this.node = node;
    }

    @Override
    public CommentedConfigurationNode node() {
        return node;
    }

    @Override
    public String toString() {
        Object value = node().getValue();
        return value != null ? value.toString() : "empty";
    }
}
