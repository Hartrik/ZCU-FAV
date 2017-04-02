package cz.zcu.kiv.pro.tree.app;

import cz.harag.utils.Resources;
import cz.zcu.kiv.pro.tree.Tree;
import cz.zcu.kiv.pro.tree.TreeNode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @version 2016-11-20
 * @author Patrik Harag
 */
public class JSTreeBuilder {

    public static class Edge {
        public int x, y;
    }

    public static class Builder {
        private final List<Edge> edges = new ArrayList<>();

        public void addEdge(int a, int b) {
            edges.add(new Edge(){{ this.x = a; this.y = b; }});
        }

        public List<Edge> getEdges() {
            return edges;
        }
    }

    private final ViewSettings viewSettings;

    public JSTreeBuilder(ViewSettings viewSettings) {
        this.viewSettings = viewSettings;
    }

    public Tree createTree(String jsCode) throws ScriptException {
        List<Edge> edges = parseEdges(jsCode);
        return createTree(edges);
    }

    public Tree createTree(List<Edge> edges) {
        Map<Integer, TreeNode> nodes = new LinkedHashMap<>();

        for (Edge edge : edges) {
            TreeNode parent, child;

            if (nodes.containsKey(edge.x)) {
                parent = nodes.get(edge.x);
            } else {
                parent = new TreeNode(edge.x);
                nodes.put(edge.x, parent);
            }

            if (nodes.containsKey(edge.y)) {
                child = nodes.get(edge.y);
            } else {
                child = new TreeNode(edge.y);
                nodes.put(edge.y, child);
            }

            if (child.getPredecessor() != null)
                throw new IllegalArgumentException("Tree node cannot have more parents");

            if (containsCycle(parent, child))
                throw new IllegalArgumentException("\"Tree\" with cycle");

            child.setPredecessor(parent);
            parent.addSuccessor(child);
        }

        Iterator<Map.Entry<Integer, TreeNode>> iterator = nodes.entrySet().iterator();
        if (!iterator.hasNext())
            return null;

        TreeNode node = iterator.next().getValue();
        while (node.getPredecessor() != null) {
            node = node.getPredecessor();
        }

        return new Tree(node);
    }

    private boolean containsCycle(TreeNode parent, TreeNode child) {
        do {
            if (parent.getId() == child.getId()) return true;
        } while ((parent = parent.getPredecessor()) != null);

        return false;
    }

    private List<Edge> parseEdges(String jsCode) throws ScriptException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("nashorn");

        Builder builder = new Builder();
        engine.put("graph", builder);
        engine.put("view", viewSettings);

        String helper = Resources.text("jsHelper.js", getClass(), StandardCharsets.UTF_8);
        engine.eval(helper + jsCode);

        return builder.getEdges();
    }

}