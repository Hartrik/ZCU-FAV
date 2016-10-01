package sp;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.junit.Test;
import sp.Graph.Node;
import sp.GraphS.SNode;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public class ConnectionsManagerTest {

    @Test
    public void testConnectionsGraphMBFS() {
        testMatrix(GraphMBFS::new);
    }

    @Test
    public void testConnectionsGraphMDFS() {
        testMatrix(GraphMDFS::new);
    }

    private void testMatrix(IntFunction<Graph<Node>> func) {
        Graph<Node> graph = func.apply(4);

        Node n1 = new Node("1");
        Node n2 = new Node("2");
        Node n3 = new Node("3");
        Node n4 = new Node("4");

        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);
        graph.addNode(n4);

        graph.connect(n1, n2);
        graph.connect(n1, n3);
        graph.connectBidirectional(n1, n4);
        graph.connect(n3, n1);
        graph.connect(n4, n4);

        assertThat(graph.connected(n1), equalTo(Arrays.asList(n4, n3, n2)));
        assertThat(graph.connected(n2), equalTo(Arrays.asList()));
        assertThat(graph.connected(n3), equalTo(Arrays.asList(n1)));
        assertThat(graph.connected(n4), equalTo(Arrays.asList(n4, n1)));
    }

    @Test
    public void testConnectionsGraphSBFS() {
        testList(GraphSBFS::new);
    }

    @Test
    public void testConnectionsGraphSDFS() {
        testList(GraphSDFS::new);
    }

    private void testList(Supplier<Graph<SNode>> func) {
        Graph<SNode> graph = func.get();

        SNode n1 = new SNode("1");
        SNode n2 = new SNode("2");
        SNode n3 = new SNode("3");
        SNode n4 = new SNode("4");

        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);
        graph.addNode(n4);

        graph.connect(n1, n2);
        graph.connect(n1, n3);
        graph.connectBidirectional(n1, n4);
        graph.connect(n3, n1);
        graph.connect(n4, n4);

        assertThat(graph.connected(n1), equalTo(Arrays.asList(n4, n3, n2)));
        assertThat(graph.connected(n2), equalTo(Arrays.asList()));
        assertThat(graph.connected(n3), equalTo(Arrays.asList(n1)));
        assertThat(graph.connected(n4), equalTo(Arrays.asList(n4, n1)));
    }

}