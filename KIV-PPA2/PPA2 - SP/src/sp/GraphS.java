package sp;

import java.util.*;
import sp.Graph.Node;
import sp.GraphS.SNode;

/**
 * Graf, který spojení (hrany) ukládá do spojového seznamu k jednotlivým
 * vrcholům.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 * @param <E> typ vrcholu
 */
public abstract class GraphS<E extends SNode> extends Graph<E> {

    @Override
    public void connect(E from, E to) {
        from.connected = new LinkedListNode(to, from.connected);
    }

    @Override
    public void connectBidirectional(E n1, E n2) {
        connect(n1, n2);
        connect(n2, n1);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<E> connected(E node) {
        // převede spojené vrcholy na Collection, aby se s nimi dalo
        // rozumě pracovat - bez sjednocení API bych se nemohl vyhnout
        // duplicitnímu kódu
        List<E> arrayList = new ArrayList<>();

        LinkedListNode temp = node.connected;
        while (temp != null) {
            arrayList.add((E) temp.node);
            temp = temp.next;
        }

        return arrayList;
    }

    public static class SNode extends Node {
        public LinkedListNode connected;

        public SNode(String value) {
            super(value);
        }
    }

    public static class LinkedListNode {
        public LinkedListNode next;
        public SNode node;

        public LinkedListNode(SNode node, LinkedListNode next) {
            this.next = next;
            this.node = node;
        }
    }

}