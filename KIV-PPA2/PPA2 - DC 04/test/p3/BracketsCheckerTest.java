package p3;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class BracketsCheckerTest {

    @Test
    public void testCheck_1() {
        assertThat(BracketsChecker.check("( a + b * c – d + e / f "), is(false));
    }

    @Test
    public void testCheck_2() {
        assertThat(BracketsChecker.check("[ ( a + b) + e ] / f }"), is(false));
    }

    @Test
    public void testCheck_3() {
        assertThat(BracketsChecker.check("()[]{}"), is(true));
    }

    @Test
    public void testCheck_4() {
        assertThat(BracketsChecker.check("{([ ])}"), is(true));
    }

    @Test
    public void testCheck_5() {
        assertThat(BracketsChecker.check("{([ )}"), is(false));
    }

    @Test
    public void testCheck_6() {
        assertThat(BracketsChecker.check("{([)))"), is(false));
    }

}