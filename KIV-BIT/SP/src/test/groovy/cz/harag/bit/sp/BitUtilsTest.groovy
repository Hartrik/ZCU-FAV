package cz.harag.bit.sp

import org.junit.Test

/**
 *
 * @version 2018-03-30
 * @author Patrik Harag
 */
class BitUtilsTest {

    @Test
    void testPermutate() {
        // Používá data z https://billstclair.com/grabbe/des.htm

        byte[] table = [
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9,  1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
        ]

        long block  = 0b00000001_00100011_01000101_01100111_10001001_10101011_11001101_11101111L
        long result = 0b11001100_00000000_11001100_11111111_11110000_10101010_11110000_10101010L
        assert BitUtils.permutate(block, table) == result
    }

    @Test
    void testExpand() {
        byte[] table = [
             1,  1,  1,  1,  1,  1,  1,  1,
             1,  1,  1,  1,  1,  1,  1,  1,
             1,  1,  1,  1,  1,  1,  1,  1,
             1,  1,  1,  1,  1,  1,  1,  1,
             1,  1,  1,  1,  1,  1,  1,  1,
             1,  1,  1,  1,  1,  1,  1,  1,
             0,  0,  0,  0,  0,  0,  0,  0,
             1,  1,  1,  1,  1,  1,  1,  1,
        ]

        assert BitUtils.expand(1 << 31, table) == 0xffffffffffff00ffL
    }

}
