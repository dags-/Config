package me.dags.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author dags <dags@dags.me>
 */
public interface Node {

    CommentedConfigurationNode node();

    default Node child(Object... path) {
        return create(node().getNode(path));
    }

    default boolean get(String key, boolean def) {
        return node().getNode(key).getBoolean(def);
    }

    default float get(String key, float def) {
        return node().getNode(key).getFloat(def);
    }

    default double get(String key, double def) {
        return node().getNode(key).getDouble(def);
    }

    default int get(String key, int def) {
        return node().getNode(key).getInt(def);
    }

    default long get(String key, long def) {
        return node().getNode(key).getLong(def);
    }

    default String get(String key, String def) {
        return node().getNode(key).getString(def);
    }

    default Node set(String key, Object value) {
        node().getNode(key).setValue(value);
        return this;
    }

    default Node set(Object value) {
        node().setValue(value);
        return this;
    }

    default Node set(List<Node> values) {
        List<CommentedConfigurationNode> list = values.stream().map(Node::node)
                .collect(Collectors.toList());
        node().setValue(list);
        return this;
    }

    default Node set(Map<Object, Node> values) {
        Map<Object, CommentedConfigurationNode> map = values.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().node()));
        node().setValue(map);
        return this;
    }

    default void add(Node... elements) {
        List<CommentedConfigurationNode> list = new ArrayList<>(node().getChildrenList());
        for (Node child : elements) {
            list.add(child.node());
        }
        node().setValue(list);
    }

    default void add(Iterable<Node> elements) {
        // todo
    }

    default void clear() {
        node().setValue(null);
    }

    default List<Node> asList() {
        return node().getChildrenList().stream()
                .map(BasicNode::new)
                .collect(Collectors.toList());
    }

    default Map<Object, Node> asMap() {
        return node().getChildrenMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new BasicNode(e.getValue())));
    }

    default <T> T bind(Class<T> type, Supplier<T> def) {
        ObjectMapper<T> mapper;

        try {
            mapper = ObjectMapper.forClass(type);
            try {
                return mapper.bindToNew().populate(node());
            } catch (ObjectMappingException e) {
                T val = def.get();
                mapper.bind(val).serialize(node());
                return val;
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return def.get();
    }

    default <T> boolean copy(T instance) {
        try {
            ObjectMapper<T>.BoundInstance mapper = ObjectMapper.forObject(instance);
            mapper.serialize(node());
            return true;
        } catch (ObjectMappingException e) {
            return false;
        }
    }

    static Node create() {
        return new BasicNode(SimpleCommentedConfigurationNode.root(Config.DEFAULT_OPTIONS));
    }

    static Node create(CommentedConfigurationNode node) {
        return new BasicNode(node);
    }
}
