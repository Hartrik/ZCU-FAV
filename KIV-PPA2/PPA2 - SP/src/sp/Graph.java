package sp;

import java.util.*;
import java.util.function.Consumer;
import sp.Graph.Node;

/**
 * Základní třída pro graf.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 * @param <N> typ vrcholu
 */
public abstract class Graph<N extends Node> {

    protected final List<N> nodes = new ArrayList<>();

    /**
     * Vytvoří a přidá nový vrchol.
     *
     * @param value hodnota vrcholu
     */
    public abstract void addNode(String value);

    /**
     * Přidá nový vrchol.
     *
     * @param node vrchol
     */
    public void addNode(N node) {
        nodes.add(node);
    }

    /**
     * Vyhledá vrchol podle hodnoty.
     *
     * @param value hodnota vrcholu
     * @return Optional
     */
    public Optional<N> findNode(String value) {
        return nodes.stream()
                .filter(node -> node.getValue().equals(value))
                .findFirst();
    }

    /**
     * Spojí dva vrcholy orientovanou hranou.
     *
     * @param value1 hodnota vrcholu 1
     * @param value2 hodnota vrcholu 2
     */
    public void connect(String value1, String value2) {
        N n1 = findNode(value1).orElseThrow(NoSuchElementException::new);
        N n2 = findNode(value2).orElseThrow(NoSuchElementException::new);
        connect(n1, n2);
    }

    /**
     * Spojí dva vrcholy neorientovanou hranou.
     *
     * @param value1 hodnota vrcholu 1
     * @param value2 hodnota vrcholu 2
     */
    public void connectBidirectional(String value1, String value2) {
        N n1 = findNode(value1).orElseThrow(NoSuchElementException::new);
        N n2 = findNode(value2).orElseThrow(NoSuchElementException::new);
        connectBidirectional(n1, n2);
    }

    /**
     * Spojí dva vrcholy orientovanou hranou (n1 -> n2).
     *
     * @param n1 vrchol 1
     * @param n2 vrchol 2
     */
    public abstract void connect(N n1, N n2);

    /**
     * Spojí dva vrcholy neorientovanou hranou (n1 <-> n2).
     *
     * @param n1 vrchol 1
     * @param n2 vrchol 2
     */
    public abstract void connectBidirectional(N n1, N n2);

    /**
     * Vrátí kolekci vrcholů spojených s tímto vrcholem.
     * (Orientovaná spojení od tohoto vrcholu.)
     *
     * @param value název vrcholu
     * @return vrcholy
     */
    public Collection<N> connected(String value) {
        return connected(findNode(value).orElseThrow(NoSuchElementException::new));
    }

    /**
     * Vrátí kolekci vrcholů spojených s tímto vrcholem.
     * (Orientovaná spojení od tohoto vrcholu.)
     *
     * @param node vrchol
     * @return vrcholy
     */
    public abstract Collection<N> connected(N node);

    /**
     * Prohledá graf z určitého vrcholu.
     *
     * @param node vrchol, ze kterého je prohledávání započato
     * @param consumer Consumer
     */
    public abstract void search(N node, Consumer<N> consumer);

    /**
     * Prohledá graf z určitého vrcholu.
     *
     * @param value hodnota vrcholu, ze kterého je prohledávání započato
     * @param consumer Consumer
     */
    public void search(String value, Consumer<N> consumer) {
        search(findNode(value).orElseThrow(RuntimeException::new), consumer);
    }

    /**
     * Vrchol grafu.
     */
    public static class Node {

        private final String value;

        // proměnné používané pro procházení
        public boolean completed;
        public Node before;
        public int distance;
        public int timeFound;
        public int timeCompleted;

        public Node(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Node{" + "value=" + value + '}';
        }
    }

}