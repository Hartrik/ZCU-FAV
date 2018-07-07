package cz.harag.bit.c03;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Patrik Harag
 */
public class CiphersTest {

    @Test
    public void testCaesar() throws Exception {
        assertThat(Ciphers.caesar("abcxyz"), is("defabc"));
    }

    @Test
    public void testAtbas() throws Exception {
        assertThat(Ciphers.atbas("abcxyz"), is("zyxcba"));
    }

    @Test
    public void testMonoSub() throws Exception {
        String a = "qwertzuiopasdfghjklyxcvbnm";
        assertThat(Ciphers.mono_sub(a, "abc"), is("qwe"));
    }

    @Test
    public void testAlberti() throws Exception {
        String a1 = "qwertzuiopasdfghjklyxcvbnm";
        String a2 = "yaqxswcdevfrbgtnhzmjukilop";
        assertThat(Ciphers.alberti(a1, a2, "abc"), is("qae"));
    }

    @Test
    public void testVigener() throws Exception {
        assertThat(Ciphers.vigener("bagr", "wikipedie"), is("xiqzqejzf"));
    }

    @Test
    public void testColTrans() throws Exception {
        assertThat(Ciphers.col_trans("klic", "cojsemnapsal"), is("sjcoanemlaps"));
    }


    @Test
    public void print() throws Exception {
        String p = "zeptaslisebudespetminutvypadatjakoblbecnezeptaslisebudesblbcempocelyzivot";

        System.out.println("caesar:   " + Ciphers.caesar(p));
        System.out.println("atbas:    " + Ciphers.atbas(p));

        String a11 = "qwertzuiopasdfghjklyxcvbnm";
        System.out.println("mono sub: " + Ciphers.mono_sub(a11, p));

        String a21 = "qwertzuiopasdfghjklyxcvbnm";
        String a22 = "yaqxswcdevfrbgtnhzmjukilop";
        System.out.println("alberti:  " + Ciphers.alberti(a21, a22, p));

        System.out.println("vigener:  " + Ciphers.vigener("d", p));

        System.out.println("s. trans: " + Ciphers.col_trans("bac", p));
    }

}
