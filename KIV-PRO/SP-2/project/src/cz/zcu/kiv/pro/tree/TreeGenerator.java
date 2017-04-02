package cz.zcu.kiv.pro.tree;

import cz.zcu.kiv.pro.tree.app.JSTreeBuilder;
import cz.zcu.kiv.pro.tree.app.JSTreeBuilder.Edge;
import cz.zcu.kiv.pro.tree.app.ViewSettings;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 2016-11-24
 * @author Patrik Harag
 */
public class TreeGenerator {

    private final int maxWidth;
    private final int maxDepth;

    private int lastID;

    public TreeGenerator(int maxWidth, int maxDepth) {
        this.maxWidth = maxWidth;
        this.maxDepth = maxDepth;
    }

    public Tree generateTree() {
        List<Edge> edges = new ArrayList<>();

        populate(lastID = 1, 1, edges);

        JSTreeBuilder builder = new JSTreeBuilder(new ViewSettings(10, 10));
        return builder.createTree(edges);
    }

    private void populate(int vertex, int depth, List<Edge> edges) {
        if (depth >= maxDepth)
            return;

        if (Math.random() * (depth / maxWidth) > .2)
            return;

        double count = Math.random() * maxWidth;
        for (int i = 0; i < count; i++) {
            int next = ++lastID;

            Edge e = new Edge();
            e.x = vertex;
            e.y = next;

            edges.add(e);
            populate(next, depth + 1, edges);
        }
    }

}