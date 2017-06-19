package cz.harag.uir.c02;

import org.junit.Test;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * @author Patrik Harag
 * @version 2017-03-03
 */
public class UIR_02_Test {

    @Test
    public void printFunctions() {
        printFunction(6, "fact. iterativně:", FactFib::facIter);
        printFunction(6, "fact. rekurzivně:", FactFib::factRec);
        printFunction(8, "fib. iterativně:", FactFib::fibIter);
        printFunction(8, "fib. rekurzivně:", FactFib::fibRec);
    }

    private void printFunction(int n, String name, IntUnaryOperator func) {
        System.out.println(name);
        IntStream.range(0, n)
                .mapToObj(i -> i + " = " + func.applyAsInt(i))
                .forEach(System.out::println);
    }

    @Test
    public void hanoi3() {
        Hanoi.hanoi(5);
    }

}
