import me.dags.config.Node;

/**
 * @author dags <dags@dags.me>
 */
public class Test1 implements Node.Value<Test1> {

    public static final Test1 EMPTY = new Test1("empty", 0, Test0.ONE);

    private final String name;
    private final int age;
    private final Test0 test;

    private Test1(String name, int age, Test0 test) {
        this.name = name;
        this.age = age;
        this.test = test;
    }

    @Override
    public Test1 fromNode(Node node) {
        String name = node.get("name", this.name);
        int age = node.get("age", this.age);
        Test0 test = node.get("test", this.test);
        return new Test1(name, age, test);
    }

    @Override
    public void toNode(Node node) {
        node.set("name", name);
        node.set("age", age);
        node.set("test", test);
    }

    @Override
    public String toString() {
        return "Test0{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", test=" + test +
                '}';
    }
}
