package p1;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static p1.HashFunctions.*;

/**
 *
 * @version 2016-04-22
 * @author Patrik Harag
 */
public class HashFunctionsTest {

    @Test
    public void testHash2() {
        assertThat(hash2_B("test", 2), is(hash2("test", 2)));
        assertThat(hash2_B("test", 5), is(hash2("test", 5)));
        assertThat(hash2_B("01225", 5), is(hash2("01225", 5)));
    }

}