package cz.harag.bit.c09;

import java.nio.ByteBuffer;

import org.junit.Test;

/**
 *
 * @author Patrik Harag
 */
public class HashTest {

    @Test
    public void c01() throws Exception {
        System.out.println(md("Kdo implementuje prvni, dostava 3 body, druhy 2 body, treti 1 bod.  "));
    }

    private String md(String text) {
        int a = 0x36;
        int b = 0x58;
        int c = 0x48;
        int d = 0x7b;

        for (int i = 0; i < text.length(); i++) {
            int m = text.charAt(i) & 0xFF;

            int f = 0;
            int k = 0;
            int s = 0;

            switch (i % 4) {
                case 0:
                    f = (b & d) | (c & (d ^ 0xFF));
                    k = 0x83;
                    s = 3;
                    break;

                case 1:
                    f = (b & c) | ((b ^ 0xFF) & d);
                    k = 0xc;
                    s = 1;
                    break;

                case 2:
                    f = c ^ (b | (d ^ 0xFF));
                    k = 0x1a;
                    s = 5;
                    break;

                case 3:
                    f = b ^ c ^ d;
                    k = 0x5c;
                    s = 2;
                    break;
            }

            a = (a + f) & 0xFF;
            a = (a + m) & 0xFF;
            a = (a + k) & 0xFF;
            a = circularLeftShift(a, 8, s);
            a = (a + b) & 0xFF;

            int tmpA = a;
            int tmpB = b;
            int tmpC = c;
            int tmpD = d;

            a = tmpD;
            b = tmpA;
            c = tmpB;
            d = tmpC;
        }

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
        return Integer.toHexString((a << 24) + (b << 16) + (c << 8) + (d));
    }

    static int circularLeftShift(int block, int blockSize, int shift) {
        int mask = 0xFFFFFFFF >>> (32 - blockSize);
        return ((block << shift) & mask) | (block >>> (blockSize - shift));
    }

}
