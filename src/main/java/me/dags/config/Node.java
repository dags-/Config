package me.dags.config;

import com.google.common.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * @author dags <dags@dags.me>
 */
public class Node {

    static final ConfigurationOptions DEFAULT_OPTIONS = ConfigurationOptions.defaults().setShouldCopyDefaults(true);

    private final CommentedConfigurationNode node;

    Node(CommentedConfigurationNode node) {
        this.node = node;
    }

    /**
     * Get the ConfigurationNode backing this node
     */
    public CommentedConfigurationNode backing() {
        return node;
    }

    /**
     * Get the child node at the path
     */
    public Node node(Object... path) {
        return create(backing().getNode(path));
    }

    /**
     * Get the comment on this node
     */
    public String comment() {
        return backing().getComment().orElse("");
    }

    /**
     * Get the value of this node
     */
    public boolean get(boolean def) {
        return backing().getBoolean(def);
    }

    /**
     * Get the value of this node
     */
    public float get(float def) {
        return backing().getFloat(def);
    }

    /**
     * Get the value of this node
     */
    public double get(double def) {
        return backing().getDouble(def);
    }

    /**
     * Get the value of this node
     */
    public int get(int def) {
        return backing().getInt(def);
    }

    /**
     * Get the value of this node
     */
    public long get(long def) {
        return backing().getLong(def);
    }

    /**
     * Get the value of this node
     */
    public String get(String def) {
        return backing().getString(def);
    }

    /**
     * Get the value of this node
     */
    public <T> T get(Deserializable<T> def) {
        return def.fromNode(this);
    }

    /**
     * Get the value of this node
     */
    public <T> T get(TypeToken<T> token, T def) {
        try {
            return backing().getValue(token, def);
        } catch (ObjectMappingException e) {
            return def;
        }
    }

    /**
     * Get the value of this node
     */
    public <T> T get(TypeToken<T> token, Supplier<T> def) {
        try {
            return backing().getValue(token, def);
        } catch (ObjectMappingException e) {
            return def.get();
        }
    }

    /**
     * Get the named value of this node
     */
    public boolean get(String key, boolean def) {
        return backing().getNode(key).getBoolean(def);
    }

    /**
     * Get the named value of this node
     */
    public float get(String key, float def) {
        return backing().getNode(key).getFloat(def);
    }

    /**
     * Get the named value of this node
     */
    public double get(String key, double def) {
        return backing().getNode(key).getDouble(def);
    }

    /**
     * Get the named value of this node
     */
    public int get(String key, int def) {
        return backing().getNode(key).getInt(def);
    }

    /**
     * Get the named value of this node
     */
    public long get(String key, long def) {
        return backing().getNode(key).getLong(def);
    }

    /**
     * Get the named value of this node
     */
    public String get(String key, String def) {
        return backing().getNode(key).getString(def);
    }

    /**
     * Get the named value of this node
     */
    public <T> T get(String key, Deserializable<T> def) {
        return node(key).get(def);
    }

    /**
     * Get the named value of this node
     */
    public <T> T get(String key, TypeToken<T> token, T def) {
        try {
            return backing().getNode(key).getValue(token);
        } catch (ObjectMappingException e) {
            return def;
        }
    }

    /**
     * Get the named value of this node
     */
    public <T> T get(String key, TypeToken<T> token, Supplier<T> def) {
        try {
            return backing().getNode(key).getValue(token);
        } catch (ObjectMappingException e) {
            return def.get();
        }
    }

    /**
     * Get the named List of T using the provided mapper function
     */
    public <T> List<T> getList(String key, Function<Node, T> mapper) {
        return node(key).getList(mapper);
    }

    /**
     * Get the List of T using the provided mapper function
     */
    public <T> List<T> getList(Function<Node, T> mapper) {
        return backing().getChildrenList().stream().map(Node::new).map(mapper).collect(Collectors.toList());
    }

    /**
     * Get the named Map of String/T pairs using the provided mapper function
     */
    public <T> Map<String, T> getMap(String key, Function<Node, T> mapper) {
        return node(key).getMap(mapper);
    }

    /**
     * Get the Map of String/T pairs using the provided mapper function
     */
    public <T> Map<String, T> getMap(Function<Node, T> mapper) {
        return backing().getChildrenMap().entrySet().stream()
                .map(e -> {
                    String key = e.getKey().toString();
                    T value = mapper.apply(new Node(e.getValue()));
                    return new HashMap.SimpleEntry<>(key, value);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get a List view of the node's children
     */
    public List<Node> childList() {
        return backing().getChildrenList().stream()
                .map(Node::new)
                .collect(Collectors.toList());
    }

    /**
     * Get a Map view of the node's children
     */
    public Map<Object, Node> childMap() {
        return backing().getChildrenMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Node(e.getValue())));
    }

    /**
     * Set the named value of this node
     */
    public Node set(String key, Object value) {
        backing().getNode(key).setValue(value);
        return this;
    }

    /**
     * Set the named value of this node
     */
    public Node set(String key, Serializable value) {
        value.toNode(node(key));
        return this;
    }

    /**
     * Set the value of this node
     */
    public Node set(Object value) {
        backing().setValue(value);
        return this;
    }

    /**
     * Set the value of this node
     */
    public Node set(Serializable value) {
        value.toNode(this);
        return this;
    }

    /**
     * Set the named Node's value to the given List
     */
    public Node set(String key, List<?> values) {
        node(key).set(values);
        return this;
    }

    /**
     * Set this Node's value to the given List
     */
    public Node set(List<?> values) {
        List<CommentedConfigurationNode> list = new ArrayList<>();
        for (Object value : values) {
            Node node;
            if (value instanceof CommentedConfigurationNode) {
                list.add((CommentedConfigurationNode) value);
                continue;
            }

            if (value instanceof Node) {
                node = (Node) value;
            } else if (value instanceof Serializable) {
                node = Node.create();
                ((Serializable) value).toNode(node);
            } else {
                try {
                    node = Node.create();
                    node.set(value);
                } catch (IllegalArgumentException iae) {
                    continue;
                }
            }
            list.add(node.backing());
        }
        backing().setValue(list);
        return this;
    }

    /**
     * Set the named Node's value to the given Map
     */
    public Node set(String key, Map<Object, ?> map) {
        node(key).set(map);
        return this;
    }

    /**
     * Set this Node's value to the given Map
     */
    public Node set(Map<Object, ?> map) {
        Map<Object, CommentedConfigurationNode> newMap = new LinkedHashMap<>();

        for (Map.Entry<Object, ?> e : map.entrySet()) {
            Object key = e.getKey();
            Object value = e.getValue();
            if (value instanceof CommentedConfigurationNode) {
                newMap.put(key, (CommentedConfigurationNode) value);
                continue;
            }

            Node node;
            if (value instanceof Node) {
                node = (Node) value;
            } else if (value instanceof Serializable) {
                node = Node.create();
                ((Serializable) value).toNode(node);
            } else {
                try {
                    node = Node.create();
                    node.set(value);
                } catch (IllegalArgumentException iae) {
                    continue;
                }
            }
            newMap.put(key, node.backing());
        }

        backing().setValue(newMap);
        return this;
    }

    /**
     * Add all elements to this List-backed node (adds to the current List)
     */
    public void addAll(Iterable<?> elements) {
        List<CommentedConfigurationNode> list = new ArrayList<>(backing().getChildrenList());
        for (Object value : elements) {
            Node node;
            if (value instanceof CommentedConfigurationNode) {
                list.add((CommentedConfigurationNode) value);
                continue;
            }

            if (value instanceof Node) {
                node = (Node) value;
            } else if (value instanceof Serializable) {
                node = Node.create();
                ((Serializable) value).toNode(node);
            } else {
                try {
                    node = Node.create();
                    node.set(value);
                } catch (IllegalArgumentException iae) {
                    continue;
                }
            }
            list.add(node.backing());
        }
        backing().setValue(list);
    }

    /**
     * Put all key/value elements to this Map-backed node (adds to the current Map)
     */
    public void putAll(Map<Object, ?> map) {
        Map<Object, CommentedConfigurationNode> newMap = new HashMap<>(backing().getChildrenMap());
        for (Map.Entry<Object, ?> e : map.entrySet()) {
            Object key = e.getKey();
            Object value = e.getValue();

            if (value instanceof CommentedConfigurationNode) {
                newMap.put(key, (CommentedConfigurationNode) value);
                continue;
            }

            Node node;
            if (value instanceof Node) {
                node = (Node) value;
            } else if (value instanceof Serializable) {
                node = Node.create();
                ((Serializable) value).toNode(node);
            } else {
                try {
                    node = Node.create();
                    node.set(value);
                } catch (IllegalArgumentException iae) {
                    continue;
                }
            }
            newMap.put(key, node.backing());
        }
        backing().setValue(newMap);
    }

    /**
     * Set the comment on this node
     */
    public void comment(String comment) {
        backing().setComment(comment);
    }

    /**
     * Clear the value on this node
     */
    public void clear() {
        backing().setValue(null);
    }

    /**
     * Check if the node is attached to it's parent
     */
    public boolean isVirtual() {
        return backing().isVirtual();
    }

    /**
     * Check if the node value is null/empty
     */
    public boolean isEmpty() {
        return backing().getValue() == null || (!backing().hasListChildren() && !backing().hasMapChildren());
    }

    /**
     * Iterate over the node's list or map values
     */
    public void iterate(Consumer<Node> consumer) {
        if (backing().hasListChildren()) {
            backing().getChildrenList().stream().map(Node::new).forEach(consumer);
        }
        if (backing().hasMapChildren()) {
            backing().getChildrenMap().values().stream().map(Node::new).forEach(consumer);
        }
    }

    /**
     * Iterate over the node's key/value pairs
     */
    public void iterate(BiConsumer<Object, Node> consumer) {
        if (backing().hasMapChildren()) {
            backing().getChildrenMap().forEach((key, value) -> {
                Node node = new Node(value);
                consumer.accept(key, node);
            });
        }
    }

    /**
     * Instantiate and populate a new value of type T
     */
    public <T> T bind(Class<T> type, T def) {
        ObjectMapper<T> mapper;

        try {
            mapper = ObjectMapper.forClass(type);
            try {
                return mapper.bindToNew().populate(backing());
            } catch (ObjectMappingException e) {
                if (def != null) {
                    mapper.bind(def).serialize(backing());
                }
                return def;
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return def;
    }

    /**
     * Instantiate and populate a new value of type T
     */
    public <T> T bind(Class<T> type, Supplier<T> def) {
        ObjectMapper<T> mapper;

        try {
            mapper = ObjectMapper.forClass(type);
            try {
                return mapper.bindToNew().populate(backing());
            } catch (ObjectMappingException e) {
                T val = def.get();
                mapper.bind(val).serialize(backing());
                return val;
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        return def.get();
    }

    /**
     * Copy the given object to this node
     */
    public <T> boolean copy(T instance) {
        try {
            ObjectMapper<T>.BoundInstance mapper = ObjectMapper.forObject(instance);
            mapper.serialize(backing());
            return true;
        } catch (ObjectMappingException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        Object value = backing().getValue();
        return value != null ? value.toString() : "empty";
    }

    /**
     * Create an empty Node
     */
    public static Node create() {
        return create(SimpleCommentedConfigurationNode.root(DEFAULT_OPTIONS));
    }

    /**
     * Create a new Node backed by the given CommentedConfigurationNode
     */
    public static Node create(CommentedConfigurationNode node) {
        return new Node(node);
    }

    /**
     * An object that can be represented in a Node tree
     */
    public interface Serializable {

        /**
         * Populates the Node with values held by the current instance
         *
         * @param node the Node to populate
         */
        void toNode(Node node);
    }

    /**
     * An object that can be initialized/populated from a Node tree
     */
    public interface Deserializable<T> {

        /**
         * Returns an instance of T populated with values from the Node.
         * May return a new instance.
         *
         * @param node the Node to populate T from
         * @return instance of T (may be a new instance or the current one)
         */
        T fromNode(Node node);
    }

    /**
     * A value that can be serialized and de-serialized to/from a Node
     *
     * @param <T> The serializable type
     */
    public interface Value<T> extends Deserializable<T>, Serializable {

    }
}
