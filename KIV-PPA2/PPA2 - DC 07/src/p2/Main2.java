package p2;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import sp.GraphS.SNode;
import sp.GraphSDFS;

/**
 *
 * @version 2016-04-09
 * @author Patrik Harag
 */
public class Main2 {

    public static void main(String[] args) {
        GraphSDFS graph = new GraphSDFS();

        // přidání uzlů
        IntStream.range(0, 7).forEach(i -> graph.addNode((char) (i + 'A') + ""));

        // přidání spojení
        graph.connect("A", "B");
        graph.connect("B", "C");

        graph.connect("D", "E");
        graph.connect("E", "F");
        graph.connect("F", "G");

        graph.connect("B", "F");

        // topologické řazení

        for (SNode n : graph.nodes) {
            if (n.completed) continue;
            graph.search(n.getValue(), (x) -> {});
        }

        String collect = graph.nodes.stream()
                .sorted(Comparator.comparing((SNode n) -> n.timeCompleted).reversed())
        //      .forEach(n -> System.out.println(n + ": " + n.timeFound + "/" + n.timeCompleted));
                .map(SNode::getValue)
                .collect(Collectors.joining(", "));

        System.out.println("G2: " + collect);

        // G2: D, E, A, B, F, G, C
    }

}