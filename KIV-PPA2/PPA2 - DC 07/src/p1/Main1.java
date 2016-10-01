package p1;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import sp.Graph.Node;
import sp.GraphS.SNode;
import sp.GraphSBFS;

/**
 *
 * @version 2016-04-09
 * @author Patrik Harag
 */
public class Main1 {

//    public static void main(String[] args) {
//        GraphSBFS graph = new GraphSBFS();
//
//        // přidání uzlů
//        IntStream.range(0, 8).forEach(i -> graph.addNode((char) (i + 'A') + ""));
//
//        // přidání spojení
//        graph.connectBidirectional("A", "B");
//        graph.connectBidirectional("A", "C");
//        graph.connectBidirectional("C", "B");
//        graph.connectBidirectional("C", "F");
//        graph.connectBidirectional("F", "G");
//        graph.connectBidirectional("C", "E");
//        graph.connectBidirectional("E", "H");
//        graph.connectBidirectional("E", "D");
//        graph.connectBidirectional("D", "H");
//
//        // vyhledání cesty
//        SNode n1 = graph.findNode("H").orElseThrow(NoSuchElementException::new);
//        SNode n2 = graph.findNode("F").orElseThrow(NoSuchElementException::new);
//        graph.search(n1, n -> {});  // prohledání do šířky
//
//        StringBuilder builder = new StringBuilder("G1:");
//        findPath(n1, n2, (n) -> {
//            if (builder.length() != 3)  // před první položku se čárka nedává
//                builder.append(",");
//
//            builder.append(" ").append(n.getValue());
//        });
//        System.out.println(builder);  // G1: H, E, C, F
//    }

    public static void main(String[] args) {
        GraphSBFS graph = new GraphSBFS();

        // přidání uzlů
        IntStream.range(0, 6).forEach(i -> graph.addNode((char) (i + 'A') + ""));

        // přidání spojení
        graph.connectBidirectional("A", "B");
        graph.connectBidirectional("B", "C");
        graph.connectBidirectional("C", "D");
        graph.connectBidirectional("B", "E");
        graph.connectBidirectional("E", "D");
        graph.connectBidirectional("E", "F");

        // vyhledání cesty
        SNode n1 = graph.findNode("A").orElseThrow(NoSuchElementException::new);
        SNode n2 = graph.findNode("F").orElseThrow(NoSuchElementException::new);
        graph.search(n1, n -> {});  // prohledání do šířky

        StringBuilder builder = new StringBuilder("G1:");
        findPath(n1, n2, (n) -> {
            if (builder.length() != 3)  // před první položku se čárka nedává
                builder.append(",");

            builder.append(" ").append(n.getValue());
        });
        System.out.println(builder);  // G1: H, E, C, F
    }

    static void findPath(Node s, Node v, Consumer<Node> consumer) {
        if (v == s) {
            consumer.accept(s);
        } else {
            if (v.before == null) {
                throw new RuntimeException("no path");
            } else {
                findPath(s, v.before, consumer);
                consumer.accept(v);
            }
        }
    }

}