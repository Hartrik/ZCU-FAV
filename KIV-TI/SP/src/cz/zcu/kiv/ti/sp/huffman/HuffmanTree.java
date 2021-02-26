package cz.zcu.kiv.ti.sp.huffman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @version 2016-11-01
 * @author Patrik Harag
 */
public class HuffmanTree {

    @FunctionalInterface
    public static interface Consumer {
        public void consume(int value, int frequency, String code);
    }

    public static HuffmanTree create(int[] frequencies) {
        Node node = HuffmanTreeBuilder.build(frequencies);

        return new HuffmanTree(node, frequencies.length);
    }

    private final Node topLevelNode;
    private final int charsetSize;

    private HuffmanTree(Node topLevelNode, int charsetSize) {
        this.topLevelNode = topLevelNode;
        this.charsetSize = charsetSize;
    }

    public Node getTopLevelNode() {
        return topLevelNode;
    }

    public int getCharsetSize() {
        return charsetSize;
    }

    public void walk(Consumer consumer) {
        // kořen může být zároveň list
        String prefix = topLevelNode.isLeaf() ? "1" : "";

        walk(topLevelNode, prefix, consumer);
    }

    private void walk(Node node, String prefix, Consumer consumer) {
        if (node.isLeaf()) {
            LeafNode leaf = (LeafNode) node;

            consumer.consume(leaf.getValue(), leaf.getFrequency(), prefix);

        } else {
            TreeNode treeNode = (TreeNode) node;

            walk(treeNode.getLeft(), prefix + '1', consumer);
            walk(treeNode.getRight(), prefix + '0', consumer);
        }
    }

    public List<Item> asList() {
        List<Item> list = new ArrayList<>(getCharsetSize());
        walk((v, f, c) -> list.add(new Item(v, f, c)));

        list.sort(Comparator.comparingInt(Item::getFrequency).reversed());

        return list;
    }

    public String[] asEncodingTable() {
        String[] table = new String[getCharsetSize()];

        walk((v, f, c) -> table[v] = c);
        return table;
    }

}