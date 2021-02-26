package cz.zcu.kiv.ti.sp.huffman;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 *
 * @version 2016-10-27
 * @author Patrik Harag
 */
public class HuffmanTreeBuilder {

    private HuffmanTreeBuilder() {
    }

    public static Node build(int[] frequencies) {
        Comparator<Node> comparator = Comparator.comparingInt(Node::getFrequency);
        PriorityQueue<Node> queue = new PriorityQueue<>(comparator);

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                queue.offer(new LeafNode(frequencies[i], i));
            }
        }

        if (queue.isEmpty())
            throw new RuntimeException("Wrong input data");

        while (queue.size() != 1) {
            // dva uzly/stromy ze začátku fronty s nejnižší celkovou frekvencí
            Node a = queue.poll();
            Node b = queue.poll();

            queue.offer(new TreeNode(a, b));
        }
        return queue.poll();
    }

}