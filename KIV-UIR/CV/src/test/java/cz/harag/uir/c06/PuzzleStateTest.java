package cz.harag.uir.c06;

import cz.hartrik.common.Point;
import org.junit.Test;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrik Harag
 * @version 2017-03-29
 */
public class PuzzleStateTest {

    @Test
    public void test() {
//        | .  #  .  . |
//        | .  .  .  # |
//        | #  .  .  . |
//        | .  .  #  . |

        PuzzleState state = new PuzzleState(4, Arrays.asList(
                Point.of(1, 0),
                Point.of(3, 1),
                Point.of(0, 2),
                Point.of(2, 3)
        ));

        assertEquals(0, state.horConflicts());
        assertEquals(0, state.verConflicts());
        assertEquals(0, state.diagConflicts());
    }

    @Test
    public void negativeTest() {
//        | .  #  .  . |
//        | .  .  .  # |
//        | #  .  .  . |
//        | .  #  .  . |

        PuzzleState state = new PuzzleState(4, Arrays.asList(
                Point.of(1, 0),
                Point.of(3, 1),
                Point.of(0, 2),
                Point.of(1, 3)
        ));

        assertEquals(1, state.horConflicts());
        assertEquals(0, state.verConflicts());
        assertEquals(2, state.diagConflicts());
    }

}
