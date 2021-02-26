package cz.zcu.kiv.ti.sp.huffman;

/**
 *
 * @version 2016-10-27
 * @author Patrik Harag
 */
public abstract class Node {

    private final int frequency;

    public Node(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public abstract boolean isLeaf();

}
