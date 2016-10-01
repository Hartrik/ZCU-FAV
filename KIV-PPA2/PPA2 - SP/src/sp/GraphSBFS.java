package sp;

import java.util.function.Consumer;
import sp.GraphS.SNode;

/**
 * Graf, který spojení (hrany) ukládá do spojového seznamu k jednotlivým
 * vrcholům, prohledává do šířky.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public class GraphSBFS extends GraphS<SNode> {

    @Override
    public void addNode(String value) {
        nodes.add(new SNode(value));
    }

    @Override
    public void search(SNode node, Consumer<SNode> consumer) {
        Iterators.BFS(node,
                (n) -> connected((SNode) n),
                (n) -> consumer.accept((SNode) n));
    }

}
