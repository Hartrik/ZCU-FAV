package cz.harag.uir.c04;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Patrik Harag
 * @version 2017-03-19
 */
public class AStarTest {

    private static int network[][] = {
            { 0, 36, 11, -1, -1},  // 0 <=> S (start)
            {36,  0, -1, 13, 43},  // 1 <=> A
            {11, -1,  0, 31, -1},  // 2 <=> B
            {-1, 13, 31,  0, 19},  // 3 <=> C
            {-1, 43, -1, 19,  0},  // 4 <=> G (end)
    };

    private static int distancesToEnd[] = {
            47,
            26,
            47,
            13,
             0,
    };

    @Test
    public void testWithHeuristics_SG() {
        int start = 0;  // S (start)
        int end = 4;    // G (end)

        AStar.Node node = AStar.find(start, end, network, i -> distancesToEnd[i]);

        assertThat(node, notNullValue());
        assertThat(node.distance, is(61));
        assertThat(node.toString(), is("0 -> 2 -> 3 -> 4"));
    }

    @Test
    public void testWithHeuristics_AG() {
        int start = 1;  // A (start)
        int end = 4;    // G (end)

        AStar.Node node = AStar.find(start, end, network, i -> distancesToEnd[i]);

        assertThat(node, notNullValue());
        assertThat(node.distance, is(32));
        assertThat(node.toString(), is("1 -> 3 -> 4"));
    }

    @Test
    public void testWithoutHeuristicsSG() {
        int start = 0;  // S (start)
        int end = 4;    // G (end)

        AStar.Node node = AStar.find(start, end, network, i -> 0);

        assertThat(node, notNullValue());
        assertThat(node.distance, is(61));
        assertThat(node.toString(), is("0 -> 2 -> 3 -> 4"));
    }
}
