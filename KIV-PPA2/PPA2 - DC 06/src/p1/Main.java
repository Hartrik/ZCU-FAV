package p1;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.Scanner;

/**
 *
 * @version 2016-03-05
 * @author Patrik Harag
 */
public class Main {

    public static void main(String[] arrstring) {

        // naplnění

        BinaryTree tree = new BinaryTree();
        int[] array = { 4, 3, 1, 6, 5, 2, 7, };
        Arrays.stream(array).forEach(tree::add);

        // výpis

        System.out.printf("%npre-order:  ");
        tree.walkPreOrder(System.out::print);

        System.out.printf("%nin-order:   ");
        tree.walkInOrder(System.out::print);

        System.out.printf("%npost-order: ");
        tree.walkPostOrder(System.out::print);

        // vyhledávání

        System.out.println("");
        System.out.println("");
        System.out.println("Vyhledat:");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextInt()) {
            System.out.print("");
            OptionalInt result = tree.find(scanner.nextInt(), System.out::print);
            System.out.print((result.isPresent() ? "" : " nenalezeno") + "\n\n");
        }
    }

}