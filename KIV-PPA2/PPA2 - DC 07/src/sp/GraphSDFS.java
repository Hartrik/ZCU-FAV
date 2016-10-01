package sp;

import java.util.function.Consumer;
import sp.GraphS.SNode;

/**
 * Graf, který spojení (hrany) ukládá do spojového seznamu k jednotlivým
 * vrcholům, prohledává do hloubky.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public class GraphSDFS extends GraphS<SNode> {

    @Override
    public void addNode(String value) {
        nodes.add(new SNode(value));
    }

    @Override
    public void search(SNode node, Consumer<SNode> consumer) {
        Iterators.DFS(node,
                (n) -> connected((SNode) n),
                (n) -> consumer.accept((SNode) n));
    }

}