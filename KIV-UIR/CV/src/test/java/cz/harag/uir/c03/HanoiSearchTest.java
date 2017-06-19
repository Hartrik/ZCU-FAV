package cz.harag.uir.c03;

import org.junit.Test;

/**
 * @author Patrik Harag
 * @version 2017-03-11
 */
public class HanoiSearchTest {

    @Test
    public void testBFS() {
        HanoiSearch.doBFS(3);
    }

    @Test
    public void testDFS() {
        HanoiSearch.doDFS(3, 10, false);
    }

    @Test
    public void testDFS_rand() {
        HanoiSearch.doDFS(3, 10, true);
    }

}
