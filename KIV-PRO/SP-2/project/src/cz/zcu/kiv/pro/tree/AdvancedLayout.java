package cz.zcu.kiv.pro.tree;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;

/**
 * Navržený algoritmus pro layout stromů.
 *
 * @version 2016-11-24
 * @author Patrik Harag
 */
public class AdvancedLayout implements Layout {

    protected final double verticalSize;
    protected final double horizontalSize;

    protected static class Bounds {
        protected int depth;
        protected double[] lefts;
        protected double[] rights;

        protected Bounds(int depth) {
            changeMaxDepth(depth);
        }

        protected final void changeMaxDepth(int newDepth) {
            double[] newLefts = new double[newDepth + 1];
            double[] newRights = new double[newDepth + 1];

            Arrays.fill(newLefts, Double.NaN);
            Arrays.fill(newRights, Double.NaN);

            if (newDepth > depth && (lefts != null && rights != null)) {
                System.arraycopy(lefts, 0, newLefts, 0, lefts.length);
                System.arraycopy(rights, 0, newRights, 0, rights.length);
            } else if (depth > newDepth) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            this.depth = newDepth;
            this.lefts = newLefts;
            this.rights = newRights;
        }
    }

    public AdvancedLayout(double hSize, double vSize) {
        this.horizontalSize = hSize;
        this.verticalSize = vSize;
    }

    @Override
    public void make(Tree tree) {
        if (tree.isEmpty()) return;

        makeSubtree(tree.getRoot(), 0);
    }

    protected Bounds makeSubtree(TreeNode node, int depth) {
        if (node.isLeaf()) {
            return layoutLeaf(node, depth);
        } else {
            return layoutTree(node, depth);
        }
    }

    protected Bounds layoutLeaf(TreeNode node, int depth) {
        node.setPosition(0, depth * verticalSize);

        Bounds bounds = new Bounds(depth);
        bounds.rights[depth] = 0 + horizontalSize;
        bounds.lefts[depth] = 0;
        return bounds;
    }

    protected Bounds layoutTree(TreeNode node, int depth) {
        int maxDepth = 0;
        Bounds treeBounds = new Bounds(depth);

        for (TreeNode successor : node.getSuccessors()) {
            Bounds subtreeBounds = makeSubtree(successor, depth + 1);

            if (subtreeBounds.depth > maxDepth) {
                maxDepth = subtreeBounds.depth;
                treeBounds.changeMaxDepth(maxDepth);
            }

            // pro každý podstrom nalezneme minimální posun
            double offset = countOffset(treeBounds, subtreeBounds, depth + 1);

            // "fyzické" posunutí podstromu na správné místo
            // - připočtení offsetu k x-ové součadnici všech uzlů
            moveSubtree(successor, offset);

            // přepočtení hranic aktuálního stromu po přidání dalšího podstromu
            recalculateBounds(treeBounds, subtreeBounds, offset);
        }

        // výpočet pozice aktuálního uzlu
        // - vycentrování nad jeho bezprostředními následníky
        DoubleSummaryStatistics s = node.getSuccessors().stream()
                .mapToDouble(TreeNode::getX)
                .summaryStatistics();

        node.setPosition(s.getMin() + (s.getMax() - s.getMin()) / 2,
                depth * verticalSize);

        // u listu je to stejné...
        treeBounds.rights[depth] = node.getX() + horizontalSize;
        treeBounds.lefts[depth] = node.getX();

        return treeBounds;
    }

    protected double countOffset(Bounds treeBounds, Bounds subtreeBounds, int depth) {
        // offset může být i záporné číslo
        double offset = Double.NEGATIVE_INFINITY;

        for (int i = depth; i <= treeBounds.depth; i++) {
            if (i >= subtreeBounds.lefts.length || Double.isNaN(subtreeBounds.lefts[i]))
                break;  // aktuální podstrom je kratší

            if (Double.isNaN(treeBounds.rights[i]))
                break;  // vlevo nic nebrání

            double subTreePos = subtreeBounds.lefts[i];
            double placedPos = treeBounds.rights[i];

            offset = Math.max(offset, placedPos - subTreePos);
        }

        return (offset == Double.NEGATIVE_INFINITY)
                ? 0  // vlevo není nic
                : offset;
    }

    protected void recalculateBounds(
            Bounds treeResult, Bounds subtreeResult, double subtreeOffset) {

        for (int i = 0; i <= subtreeResult.depth; i++) {
            // posunutí levé hranice
            double oldLeftBound = treeResult.lefts[i];
            double newLeftBound = subtreeOffset + subtreeResult.lefts[i];

            treeResult.lefts[i] = Double.isNaN(oldLeftBound)
                    ? newLeftBound
                    : Math.min(oldLeftBound, newLeftBound);

            // posunutí pravé hranice
            double oldRightBound = treeResult.rights[i];
            double newRightBound = subtreeOffset + subtreeResult.rights[i];

            treeResult.rights[i] = Double.isNaN(oldRightBound)
                    ? newRightBound
                    : Math.max(oldRightBound, newRightBound);
        }
    }

    protected void moveSubtree(TreeNode node, final double offset) {
        node.walk(n -> n.setX(n.getX() + offset));
    }

}