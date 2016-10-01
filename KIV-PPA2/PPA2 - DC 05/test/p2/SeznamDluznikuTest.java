package p2;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @version 2016-03-18
 * @author Patrik Harag
 */
public class SeznamDluznikuTest {

    @Test
    public void test_1() {
        SeznamDluzniku dluznici = new SeznamDluzniku();

        dluznici.pridat("Pavel", 100)
                .pridat("Eva", 25)
                .pridat("Pavel", 200)
                .pridat("Jana", 50)
                .pridat("Pavel", 15);

        dluznici.propojit();

        assertThat(dluznici.secti(), is(390));
        assertThat(dluznici.secti("Pavel"), is(315));
        assertThat(dluznici.secti("Jana"), is(50));
        assertThat(dluznici.secti("Eva"), is(25));
    }

}