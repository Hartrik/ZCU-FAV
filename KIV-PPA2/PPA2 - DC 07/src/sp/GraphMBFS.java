package sp;

import java.util.function.Consumer;
import sp.Graph.Node;

/**
 * Graf, který spojení (hrany) ukládá do matice a prohledává do šířky.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public class GraphMBFS extends GraphM<Node> {

    public GraphMBFS(int capacity) {
        super(capacity);
    }

    @Override
    public void addNode(String value) {
        nodes.add(new Node(value));
    }

    @Override
    public void search(Node node, Consumer<Node> consumer) {
        Iterators.BFS(node, this::connected, consumer);
    }

}