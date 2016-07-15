package cz.hartrik.ppa1.sort;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @version 2015-10-31
 * @author Patrik Harag
 */
public class RndTest {

    @Test
    public void testIterator() {
        Rnd random = new Rnd(67);
        String all = "";
        for (Integer rndInt : random) {
            all += rndInt + " ";
        }

        assertThat(all, is("67 45 21 "));
    }

    @Test
    public void testLongSequence_1() {
        Rnd random = new Rnd(10);

        assertArrayEquals(random.toArray(),
                new int[] {10, 11, 13, 17, 29, 85, 73, 54, 30, 91, 83, 69, 48, 24, 58, 34, 12, 15, 23, 53});
    }

    @Test
    public void testLongSequence_2() {
        Rnd random = new Rnd(83);

        assertArrayEquals(random.toArray(),
                new int[] {83, 69, 48, 24, 58, 34, 12, 15, 23, 53, 29, 85, 73, 54, 30, 91});
    }
}