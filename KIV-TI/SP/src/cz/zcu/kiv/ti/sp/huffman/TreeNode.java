package cz.zcu.kiv.ti.sp.huffman;

/**
 *
 * @version 2016-10-27
 * @author Patrik Harag
 */
public class TreeNode extends Node {

    private final Node left;
    private final Node right;

    public TreeNode(Node left, Node right) {
        super(left.getFrequency() + right.getFrequency());
        this.left = left;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

}
