package cz.harag.uir.c02;

/**
 *
 * @author Patrik Harag
 * @version 2017-03-03
 */
public class Hanoi {

    public static void hanoi(int n) {
        hanoi(n, 1, 2, 3);
    }

    private static void hanoi(int n, int peg1, int peg2, int peg3) {
        if (n == 1) {
            printMove(1, peg1, peg3);
        } else {
            hanoi(n - 1, peg1, peg3, peg2);
            printMove(n, peg1, peg3);
            hanoi(n - 1, peg2, peg1, peg3);
        }
    }

    private static void printMove(int i, int from, int to) {
        System.out.printf("%c | %s -> %s%n", i + 'A' - 1, from, to);
    }

}
