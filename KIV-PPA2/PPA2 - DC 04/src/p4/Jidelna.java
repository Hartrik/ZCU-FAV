
package p4;

import java.util.stream.Stream;
import p2.Queue;

/**
 *
 * @version 2016-03-09
 * @author Patrik Harag
 */
public class Jidelna {

    public static void main(String[] args) {
        jidelna(8,
                new Stravnik("Petr", 2),
                new Stravnik("Pavel", 3),
                new Stravnik("Jirka", 2));

        System.out.println("------");

        jidelna(8,
                new Stravnik("Petr", 3),
                new Stravnik("Pavel", 4),
                new Stravnik("Jirka", 2));
    }

    private static void jidelna(int jidlo, Stravnik... stravnici) {
        Queue<Stravnik> queue = new Queue<>();
        Stream.of(stravnici).forEach(queue::enqueue);

        for (; jidlo > 0; jidlo--) {
            if (queue.isEmpty())  break;

            Stravnik s = queue.dequeue();
            s.snezChutovku();

            if (!s.nasycen())
                queue.enqueue(s);
        }

        System.out.println("Syti:");
        Stream.of(stravnici)
                .filter(Stravnik::nasycen)
                .map(Stravnik::getJmeno)
                .forEach(s -> System.out.println(" " + s));

        System.out.println("Hladovi:");
        while (!queue.isEmpty()) {
            Stravnik s = queue.dequeue();
            System.out.printf(
                    " %s ma hlad na %d chutovek%n", s.getJmeno(), s.getHlad());
        }

        System.out.printf("Zbylo %d chutovek%n", jidlo);
    }

}