package cz.harag.uir.c04;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.IntUnaryOperator;

/**
 *
 * @author Patrik Harag
 * @version 2017-03-19
 */
class AStar {

    static Node find(int start, int end, int[][] network, IntUnaryOperator h) {
        PriorityQueue<Node> queue = new PriorityQueue<>(
                Comparator.comparingInt(n -> n.distance + n.heuristics));

        queue.add(new Node() {{ this.index = start; }});

        while (!queue.isEmpty()) {
            Node node = queue.poll();

            if (node.index == end)
                return node;

            for (int i = 0; i < network.length; i++) {
                int connection = network[node.index][i];
                if (connection > 0) {
                    // connection exists & different node

                    final int idx = i;
                    queue.add(new Node() {{
                        this.index = idx;
                        this.distance = node.distance + connection;
                        this.heuristics = h.applyAsInt(idx);
                        this.parent = node;
                    }});
                }
            }
        }

        return null;
    }

    static class Node {
        int index;
        int distance;
        int heuristics;
        Node parent;

        @Override
        public String toString() {
            return (parent == null ? "" : parent.toString() + " -> ") + index;
        }
    }

}
