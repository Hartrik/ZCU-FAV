package p2;

import java.util.LinkedList;
import java.util.Queue;
import p1.BinaryTree;
import p1.Node;

/**
 * Implementace vyváženého binárního stromu.
 *
 * @version 2016-03-19
 * @author Patrik Harag
 */
public class BalancedBinaryTree extends BinaryTree {

    @Override
    public void add(int n) {
        Node newNode = new Node(n);

        if (root == null) {
            root = newNode;

        } else {
            Queue<Node> queue = new LinkedList<>();
            queue.add(root);

            while (!queue.isEmpty()) {
                Node next = queue.poll();

                if (next.left == null) {
                    next.left = newNode;
                    return;
                }

                if (next.right == null) {
                    next.right = newNode;
                    return;
                }

                if (next.left != null)
                    queue.add(next.left);

                if (next.right != null)
                    queue.add(next.right);

            }
        }
    }

}