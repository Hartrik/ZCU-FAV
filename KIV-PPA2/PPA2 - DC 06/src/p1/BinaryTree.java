package p1;

import java.util.OptionalInt;
import java.util.function.IntConsumer;

/**
 * Implementace binárního stromu.
 *
 * @version 2016-03-05
 * @author Patrik Harag
 */
public class BinaryTree {

    protected Node root = null;

    public boolean isEmpty() {
        return root == null;
    }

    public void add(int n) {
        if (isEmpty())
            this.root = new Node(n);
        else
            this.root = add(root, n);
    }

    private Node add(Node node, int n) {
        if (node == null)
            return new Node(n);

        if (n < node.data)
            node.left = add(node.left, n);
        else
            node.right = add(node.right, n);

        return node;
    }

    // metody pro průchod stromem

    public void walkPreOrder(IntConsumer consumer) {
        walkPreOrder(root, consumer);
    }

    public void walkPreOrder(Node node, IntConsumer consumer) {
        if (node == null) return;

        consumer.accept(node.data);
        walkPreOrder(node.left, consumer);
        walkPreOrder(node.right, consumer);
    }

    public void walkInOrder(IntConsumer consumer) {
        walkInOrder(root, consumer);
    }

    public void walkInOrder(Node node, IntConsumer consumer) {
        if (node == null) return;

        walkInOrder(node.left, consumer);
        consumer.accept(node.data);
        walkInOrder(node.right, consumer);
    }

    public void walkPostOrder(IntConsumer consumer) {
        walkPostOrder(root, consumer);
    }

    public void walkPostOrder(Node node, IntConsumer consumer) {
        if (node == null) return;

        walkPostOrder(node.left, consumer);
        walkPostOrder(node.right, consumer);
        consumer.accept(node.data);
    }

    // vyhledávání

    public OptionalInt find(int n, IntConsumer consumer) {
        return find(root, n, consumer);
    }

    private OptionalInt find(Node node, int n, IntConsumer consumer) {
        if (node == null)
            return OptionalInt.empty();

        consumer.accept(node.data);

        if (n < node.data)
            return find(node.left, n, consumer);
        else if (n > node.data)
            return find(node.right, n, consumer);
        else
            return OptionalInt.of(node.data);
    }

}