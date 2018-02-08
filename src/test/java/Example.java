import me.dags.config.Config;
import me.dags.config.Node;

/**
 * @author dags <dags@dags.me>
 */
public class Example {

    public static void main(String[] args) {
        Config config = Config.must("config.conf");

        Node people = config.child("people");
        if (people.isEmpty()) {
            people.add(Node.create().set("name", "Harry").set("age", 25));
            people.add(Node.create().set("name", "Mary").set("age", 32));
        }
        System.out.println(people);

        Node locations = config.child("locations");
        int hTime = locations.child("here", "time").get(600);
        int tTime = locations.child("there", "time").get(800);
        System.out.println(hTime + " - " + tTime);

        config.save();
    }
}
