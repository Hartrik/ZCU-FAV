
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class BinarySearchTest {

    @Test
    public void testContains() {
        assertThat(BinarySearch.contains(1, new int[] {1, 2, 4, 5}), is(true));
        assertThat(BinarySearch.contains(2, new int[] {1, 2, 4, 5}), is(true));
        assertThat(BinarySearch.contains(4, new int[] {1, 2, 4, 5}), is(true));
        assertThat(BinarySearch.contains(5, new int[] {1, 2, 4, 5}), is(true));

        assertThat(BinarySearch.contains(5, new int[] {1, 2, 2, 4, 5}), is(true));
        assertThat(BinarySearch.contains(1, new int[] {1, 2, 2, 4, 5}), is(true));
        assertThat(BinarySearch.contains(4, new int[] {1, 2, 2, 4, 5}), is(true));
        assertThat(BinarySearch.contains(2, new int[] {1, 2, 2, 4, 5}), is(true));

        assertThat(BinarySearch.contains(0, new int[] {1, 2, 2, 4, 5}), is(false));
        assertThat(BinarySearch.contains(3, new int[] {1, 2, 2, 4, 5}), is(false));
        assertThat(BinarySearch.contains(6, new int[] {1, 2, 2, 4, 5}), is(false));
    }

}
