package cz.harag.uir.c02;

import java.util.stream.IntStream;

/**
 *
 * @author Patrik Harag
 * @version 2017-02-27
 */
public class FactFib {

    // Faktoriál

    public static int facIter(int n) {
        return IntStream.rangeClosed(1, n).reduce(1, (i, j) -> i*j);
    }

    public static int factRec(int n) {
        return (n < 2) ? 1 : n*factRec(n - 1);
    }

    // Fibonacciho posloupnost

    public static int fibIter(int n) {
        if (n < 1) return 0;

        int a = 0;
        int b = 1;

        for (int i = 2; i <= n; i++) {
            b = a + (a = b);

            // int temp = b;
            // b = a + b;
            // a = temp;
        }

        return b;
    }

    public static int fibRec(int n) {
        if (n == 0)
            return 0;
        else if (n == 1)
            return 1;
        else
            return fibRec(n - 1) + fibRec(n - 2);
    }

}
