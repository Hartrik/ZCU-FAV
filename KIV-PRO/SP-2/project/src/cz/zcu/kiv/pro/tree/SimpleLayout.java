package cz.zcu.kiv.pro.tree;

/**
 *
 * @version 2016-11-21
 * @author Patrik Harag
 */
public class SimpleLayout implements Layout {

    private final double verticalSize;
    private final double horizontalSize;

    public SimpleLayout(double hSize, double vSize) {
        this.horizontalSize = hSize;
        this.verticalSize = vSize;
    }

    @Override
    public void make(Tree tree) {
        if (tree.isEmpty()) return;

        makeSubTree(tree.getRoot(), 0, 0);
    }

    private double makeSubTree(TreeNode node, double left, int level) {
        if (node.isLeaf()) {
            node.setPosition(left + horizontalSize/2, level * verticalSize);
            return horizontalSize;

        } else {
            double size = 0;
            for (TreeNode successor : node.getSuccessors()) {
                size += makeSubTree(successor, left + size, level + 1);
            }

            node.setPosition(left + size/2, level * verticalSize);
            return size;
        }
    }

}