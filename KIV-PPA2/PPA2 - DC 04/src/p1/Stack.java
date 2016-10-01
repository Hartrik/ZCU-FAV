package p1;


import java.util.NoSuchElementException;


/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class Stack <T> {

    private Node node = null;

    public void push(T obj) {
        this.node = new Node() {{ object = obj; next = node; }};
    }

    public T pop() {
        if (isEmpty()) throw new NoSuchElementException();

        T val = node.object;
        node = node.next;
        return val;
    }

    public boolean isEmpty() {
        return node == null;
    }

    private class Node {
        T object;
        Node next;
    }

}