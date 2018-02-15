import me.dags.config.Node;

/**
 * @author dags <dags@dags.me>
 */
public class Test2 implements Node.Value<Test2> {

    public static final Test2 EMPTY = new Test2(Test1.EMPTY, 0);

    private final Test1 test;
    private final int age;

    public Test2(Test1 test, int age) {
        this.test = test;
        this.age = age;
    }

    @Override
    public Test2 fromNode(Node node) {
        Test1 test = node.get("test", this.test);
        int age = node.get("age", this.age);
        return new Test2(test, age);
    }

    @Override
    public void toNode(Node node) {
        node.set("age", age);
        node.set("test", test);
    }

    @Override
    public String toString() {
        return "Test1{" +
                "test=" + test +
                ", age=" + age +
                '}';
    }
}
