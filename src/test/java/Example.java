import me.dags.config.Config;

/**
 * @author dags <dags@dags.me>
 */
public class Example {

    public static void main(String[] args) {
        Config config = Config.must("config.conf");

        Test0 test0 = config.get("test", Test0.ONE);
        Test1 test1 = config.get("test0", Test1.EMPTY);
        Test2 test2 = config.get("test2", Test2.EMPTY);
        Test2 test3 = config.get("test1", n -> {
            Test1 t1 = n.get("test", test1);
            int age = n.get("age", 1234);
            return new Test2(t1, age);
        });

        config.iterate((k, v) -> System.out.println(k + "=" + v));
        System.out.println();
        System.out.println(test0);
        System.out.println(test1);
        System.out.println(test2);
        System.out.println(test3);

        config.save();
    }
}
