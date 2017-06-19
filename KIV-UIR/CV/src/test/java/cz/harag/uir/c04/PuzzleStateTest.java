package cz.harag.uir.c04;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Patrik Harag
 * @version 2017-03-19
 */
public class PuzzleStateTest {

    // --- example - empty

    @Test
    public void testEmpty() {
        PuzzleState puzzleState = new PuzzleState(new Boolean[][]{
            new Boolean[] { false, false, false },
            new Boolean[] { false, false, false },
            new Boolean[] { false, false, false },
        });

        assertTrue(puzzleState.canSetAt(0, 0));
        assertTrue(puzzleState.canSetAt(0, 1));
        assertTrue(puzzleState.canSetAt(0, 2));

        assertTrue(puzzleState.canSetAt(1, 0));
        assertTrue(puzzleState.canSetAt(1, 1));
        assertTrue(puzzleState.canSetAt(1, 2));

        assertTrue(puzzleState.canSetAt(2, 0));
        assertTrue(puzzleState.canSetAt(2, 1));
        assertTrue(puzzleState.canSetAt(2, 2));
    }

    // --- example - 1

    private final PuzzleState example1 = new PuzzleState(new Boolean[][]{
            new Boolean[] { false, false, false, false },
            new Boolean[] { false, false, true,  false },
            new Boolean[] { false, false, false, false },
            new Boolean[] { false, false, false, false },
    });

    @Test
    public void testCenter() {
        assertFalse(example1.canSetAt(2, 1));
    }

    @Test
    public void testHorizontal() {
        assertFalse(example1.canSetAt(0, 1));
        assertFalse(example1.canSetAt(1, 1));
        assertFalse(example1.canSetAt(2, 1));
        assertFalse(example1.canSetAt(3, 1));
    }

    @Test
    public void testVertical() {
        assertFalse(example1.canSetAt(2, 0));
        assertFalse(example1.canSetAt(2, 1));
        assertFalse(example1.canSetAt(2, 2));
        assertFalse(example1.canSetAt(2, 3));
    }

    @Test
    public void testTopRight() {
        assertFalse(example1.canSetAt(1, 0));
        assertFalse(example1.canSetAt(2, 1));
        assertFalse(example1.canSetAt(3, 2));
    }

    @Test
    public void testTopLeft() {
        assertFalse(example1.canSetAt(3, 0));
        assertFalse(example1.canSetAt(2, 1));
        assertFalse(example1.canSetAt(1, 2));
        assertFalse(example1.canSetAt(0, 3));
    }

    // --- example - 2

    private final PuzzleState example2 = new PuzzleState(new Boolean[][]{
            new Boolean[] { true,  false, false, false },
            new Boolean[] { false, false, false, false },
            new Boolean[] { false, false, false, false },
            new Boolean[] { false, false, false, false },
    });

    @Test
    public void testTopRight2() {
        assertFalse(example2.canSetAt(0, 0));
        assertFalse(example2.canSetAt(1, 1));
        assertFalse(example2.canSetAt(2, 2));
        assertFalse(example2.canSetAt(3, 3));
    }

    // --- example - 3

    private final PuzzleState example3 = new PuzzleState(new Boolean[][]{
            new Boolean[] { false, false, false, false },
            new Boolean[] { false, false, false, false },
            new Boolean[] { false, false, false, false },
            new Boolean[] { true,  false, false, false },
    });

    @Test
    public void testTopLeft2() {
        assertFalse(example3.canSetAt(0, 3));
        assertFalse(example3.canSetAt(1, 2));
        assertFalse(example3.canSetAt(2, 1));
        assertFalse(example3.canSetAt(3, 0));
    }

}
