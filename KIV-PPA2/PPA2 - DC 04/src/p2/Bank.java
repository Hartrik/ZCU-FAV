package p2;

import java.util.Arrays;


/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class Bank {

    public static void main(String[] args) {
        simulate("ppppppok");

        System.out.println("-----------");

        simulate("ppopooo");

        System.out.println("-----------");

        simulate("ppoppoook");
    }

    private static void simulate(String actions) {
        Queue<Character> queue = new Queue<>();

        char next = 'A';
        for (char c : actions.toCharArray()) {
            System.out.println("Akce: " + c);

            if (c == 'p') {
                queue.enqueue(next++);
                System.out.println(Arrays.toString(queue.getArray()));

            } else if (c == 'o') {
                if (queue.isEmpty()) {
                    System.out.println("Chyba - fronta je prazdna");
                } else {
                    Character character = queue.dequeue();

                    // System.out.println("<< " + character);
                    System.out.println(Arrays.toString(queue.getArray()));
                }

            } else if (c == 'k') {
                if (!queue.isEmpty())
                    System.out.println("Ve frontě někdo zbyl!");
            }
        }
    }

}