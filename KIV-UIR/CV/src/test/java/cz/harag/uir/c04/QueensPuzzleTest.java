package cz.harag.uir.c04;

import org.junit.Test;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Patrik Harag
 * @version 2017-03-19
 */
public class QueensPuzzleTest {

    private static void print(Set<PuzzleState> solutions) {
        solutions.forEach(s -> {
            System.out.println();
            System.out.println(s);
        });
    }

    @Test
    public void test4x4() {
        Set<PuzzleState> solutions = QueensPuzzle.solve(4);

        //print(solutions);
        assertThat(solutions.size(), is(2));
    }

    @Test
    public void test5x5() {
        Set<PuzzleState> solutions = QueensPuzzle.solve(5);

        //print(solutions);
        assertThat(solutions.size(), is(10));
    }

    @Test
    public void test8x8() {
        Set<PuzzleState> solutions = QueensPuzzle.solve(8);

        //print(solutions);
        assertThat(solutions.size(), is(92));
    }

}
