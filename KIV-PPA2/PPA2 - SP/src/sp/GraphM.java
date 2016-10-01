package sp;

import java.util.Collection;
import java.util.LinkedList;
import sp.Graph.Node;

/**
 * Graf, který spojení (hrany) ukládá do matice.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 * @param <E> typ vrcholu
 */
public abstract class GraphM<E extends Node> extends Graph<E> {

    private final int capacity;
    private final boolean[][] matrix;

    /**
     * Vytvoří nový graf s určitým maximálním počtem vrcholů.
     *
     * @param capacity max počet vrcholů
     */
    public GraphM(int capacity) {
        this.capacity = capacity;
        this.matrix = new boolean[capacity][capacity];
    }

    @Override
    public void connect(E from, E to) {
        int i = nodes.indexOf(from);
        int j = nodes.indexOf(to);

        matrix[i][j] = true;
    }

    @Override
    public void connectBidirectional(E node1, E node2) {
        int i = nodes.indexOf(node1);
        int j = nodes.indexOf(node2);

        matrix[i][j] = true;
        matrix[j][i] = true;
    }

    @Override
    public Collection<E> connected(E node) {
        int index = nodes.indexOf(node);
        boolean[] array = matrix[index];
        LinkedList<E> out = new LinkedList<>();

        // úmyslně vkládá vrcholy opačně, aby to souhlasilo se vzorem...

        for (int i = 0; i < capacity; i++)
            if (array[i]) out.addFirst(nodes.get(i));

        return out;
    }

}