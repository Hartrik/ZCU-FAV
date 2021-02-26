package cz.zcu.kiv.ti.sp;

import cz.zcu.kiv.ti.sp.huffman.HuffmanTree;
import cz.zcu.kiv.ti.sp.huffman.LeafNode;
import cz.zcu.kiv.ti.sp.huffman.Node;
import cz.zcu.kiv.ti.sp.huffman.TreeNode;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Třída pro dekódování
 *
 * @version 2016-11-19
 * @author Jakub Vasta
 * @author Patrik Harag
 */
public class Decoder {

    /** Strom používaný na dekódování */
    private final HuffmanTree tree;

    /** Consumer přebírající dekódované bajty */
    private final IntConsumer consumer;

    /* Požadovaný počet dekódovaných bajtů. */
    private final int expectedTotalBytes;

    /* Aktuální počet dekódovaných bajtů */
    private int totalBytes;

    /** Prvek na kterém se zrovna nacházíme */
    private Node node = null;

    /**
     * Vytváření instance Decoder a inicializace stromu a prvku kde se nacházíme
     *
     * @param frequencies tabulka četností
     * @param consumer
     */
    public Decoder(int[] frequencies, IntConsumer consumer) {
        this.tree = HuffmanTree.create(frequencies);
        this.consumer = consumer;
        this.expectedTotalBytes = IntStream.of(frequencies).sum();
        this.node = tree.getTopLevelNode();

        if (Main.info) {
            Main.printTable(this.tree);
            System.out.println("N - Načtený vstup [hexadecimálně], D - dekódovaný symbol ");
        }
    }

    /**
     * Metoda pro dekódování
     *
     * @param nextByte bajt (jako int) na dekódování
     * @throws IOException
     */
    public void decode(int nextByte) throws IOException {
        if (Main.info)
            System.out.printf("N %h\n", nextByte);

        decode(((nextByte >> 7) & 1) == 1);
        decode(((nextByte >> 6) & 1) == 1);
        decode(((nextByte >> 5) & 1) == 1);
        decode(((nextByte >> 4) & 1) == 1);

        decode(((nextByte >> 3) & 1) == 1);
        decode(((nextByte >> 2) & 1) == 1);
        decode(((nextByte >> 1) & 1) == 1);
        decode(((nextByte)      & 1) == 1);
    }

    public void decode(boolean next) throws IOException {
        if (totalBytes == expectedTotalBytes) return;

        if (node.isLeaf()) {
            // kořen je zároveň list
        } else {
            node = (next) ? ((TreeNode) node).getLeft()
                          : ((TreeNode) node).getRight();
        }

        if (node.isLeaf()) {
            consumer.accept(((LeafNode) node).getValue());
            totalBytes++;

            if(Main.info)
                System.out.println("D " + ((LeafNode) node).getValue());

            node = tree.getTopLevelNode();
        }
    }

}