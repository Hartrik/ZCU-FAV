package cz.zcu.kiv.ti.sp.huffman;

/**
 *
 * @version 2016-10-27
 * @author Patrik Harag
 */
public class LeafNode extends Node {

    private final int value;

    public LeafNode(int frequency, int value) {
        super(frequency);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

}
