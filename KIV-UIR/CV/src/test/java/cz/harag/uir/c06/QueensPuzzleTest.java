package cz.harag.uir.c06;

import org.junit.Test;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrik Harag
 * @version 2017-03-29
 */
public class QueensPuzzleTest {

    private final Random random = new Random(454);
    private final QueensPuzzle problem = new QueensPuzzle(random);

    private void print(PuzzleState result) {
        System.out.println();
        System.out.println(result.getQueens());
        System.out.println(result.getFitness() + " /conflicts = " + result.conflicts());
    }

    @Test
    public void test4x4() {
        PuzzleState result = problem.fill(4);

        // print(result);
        assertEquals(0, result.conflicts());
    }

    @Test
    public void test5x5() {
        PuzzleState result = problem.fill(5);

        // print(result);
        assertEquals(0, result.conflicts());
    }

    @Test
    public void test8x8() {
        PuzzleState result = problem.fill(8);

        // print(result);
        assertEquals(0, result.conflicts());
    }

}
