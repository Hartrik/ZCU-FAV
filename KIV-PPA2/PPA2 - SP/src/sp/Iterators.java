package sp;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import sp.Graph.Node;

/**
 * Třída obsahuje metody pro prohledávání grafu.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public final class Iterators {

    private Iterators() { }

    /**
     * Implementace prohledávání do šířky (Breadth-first search).
     *
     * @param startNode vrchol, od kterého se bude prohledávat
     * @param connectionsSupplier funkce vracející "sousedy" daného vrcholu
     * @param consumer funkce přebírající nalezené vrcholy
     */
    public static void BFS(
            Node startNode,
            Function<? super Node, Collection<? extends Node>> connectionsSupplier,
            Consumer<? super Node> consumer) {

        Queue<Node> queue = new LinkedList<>();

        startNode.completed = true;
        startNode.distance = 0;

        consumer.accept(startNode);
        queue.add(startNode);

        while(!queue.isEmpty()) {
            Node node = queue.poll();
            for (Node v : connectionsSupplier.apply(node)) {
                if (!v.completed) {
                    v.completed = true;
                    v.distance = node.distance + 1;
                    v.before = node;

                    consumer.accept(v);
                    queue.add(v);
                }
            }
        }
    }

    /**
     * Implementace prohledávání do hloubky (Depth-first search).
     *
     * @param startNode vrchol, od kterého se bude prohledávat
     * @param connectionsSupplier funkce vracející "sousedy" daného vrcholu
     * @param consumer funkce přebírající nalezené vrcholy
     */
    public static void DFS(
            Node startNode,
            Function<? super Node, Collection<? extends Node>> connectionsSupplier,
            Consumer<? super Node> consumer) {

        recur(startNode, 0, connectionsSupplier, consumer);
    }

    private static void recur(
            Node node,
            int time,
            Function<? super Node, Collection<? extends Node>> connectionsSupplier,
            Consumer<? super Node> consumer) {

        node.completed = true;
        node.timeFound = time = time + 1;
        consumer.accept(node);

        for (Node v : connectionsSupplier.apply(node)) {
            if (!v.completed) {
                v.before = node;
                recur(v, time, connectionsSupplier, consumer);
            }
        }
        node.timeCompleted = time = time + 1;
    }

}