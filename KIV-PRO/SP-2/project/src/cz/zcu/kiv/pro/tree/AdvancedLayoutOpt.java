package cz.zcu.kiv.pro.tree;

/**
 * Vylepšený algoritmus pro layout stromů.
 * Má navíc další průchod při kterém dokončí posouvání podstromů.
 *
 * @version 2016-11-24
 * @author Patrik Harag
 */
public class AdvancedLayoutOpt extends AdvancedLayout {

    public AdvancedLayoutOpt(double hSize, double vSize) {
        super(hSize, vSize);
    }

    @Override
    public void make(Tree tree) {
        if (tree.isEmpty()) return;

        TreeNode root = tree.getRoot();

        makeSubtree(root, 0);
        finalizeMove(root, 0);
    }

    @Override
    protected void moveSubtree(TreeNode node, double offset) {
        // posune pouze tento uzel, zbytek se posune později
        node.setX(node.getX() + offset);

        if (node.isLeaf())
            return;

        node.setTemp(offset);
    }

    private void finalizeMove(TreeNode node, double totalOffset) {
        if (node.isLeaf())
            return;

        totalOffset += node.getTemp();
        node.setTemp(0);  // vynulování kvůli dalšímu vykreslování

        for (TreeNode successor : node.getSuccessors()) {
            successor.setX(successor.getX() + totalOffset);
            finalizeMove(successor, totalOffset);
        }
    }

}