package p2;

import java.util.Arrays;
import p1.BinaryTree;

/**
 *
 * @version 2016-03-19
 * @author Patrik Harag
 */
public class Main {

    public static void main(String[] args) {

        // naplnění

        BinaryTree tree = new BalancedBinaryTree();

        int[] array = { 1, 6, 7, 4, 2, 3 };
        Arrays.stream(array).forEach(tree::add);

        // výpis

        System.out.printf("%npre-order:  ");
        tree.walkPreOrder(System.out::print);

        System.out.printf("%nin-order:   ");
        tree.walkInOrder(System.out::print);

        System.out.printf("%npost-order: ");
        tree.walkPostOrder(System.out::print);
    }

}