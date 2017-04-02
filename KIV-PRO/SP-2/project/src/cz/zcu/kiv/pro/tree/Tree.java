package cz.zcu.kiv.pro.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @version 2016-11-20
 * @author Patrik Harag
 */
public class Tree {

    private final TreeNode root;
    private final List<TreeNode> nodes = new ArrayList<>();

    public Tree(TreeNode root) {
        this.root = root;

        if (root != null)
            root.walk(nodes::add);
    }

    public List<TreeNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public TreeNode getRoot() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }

}