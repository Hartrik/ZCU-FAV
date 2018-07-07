package cz.harag.bit.sp;

/**
 * <p>Implementace jádra DES.</p>
 *
 * <p>Zdroj tabulek:</p>
 * <pre>
 * U.S. DEPARTMENT OF COMMERCE/National Institute of Standards and Technology
 * DATA ENCRYPTION STANDARD (DES)
 * https://csrc.nist.gov/csrc/media/publications/fips/46/3/archive/1999-10-25/documents/fips46-3.pdf
 * </pre>
 *
 * @author Patrik Harag
 * @version 2018-03-30
 */
class DESCore {

    private DESCore() {
        // skrytý konstruktor
    }

    /**
     * Tabulka pro počáteční permutaci.
     *
     * Bity vstupního bloku jsou nejprve zpřeházeny podle následující tabulky.
     * Např. na první pozici je přesunut bit z 58 pozice...
     *
     * @see BitUtils#permutate(long, byte[])
     */
    final static byte[] IP = {
            58, 50, 42, 34, 26, 18, 10,  2,
            60, 52, 44, 36, 28, 20, 12,  4,
            62, 54, 46, 38, 30, 22, 14,  6,
            64, 56, 48, 40, 32, 24, 16,  8,
            57, 49, 41, 33, 25, 17,  9,  1,
            59, 51, 43, 35, 27, 19, 11,  3,
            61, 53, 45, 37, 29, 21, 13,  5,
            63, 55, 47, 39, 31, 23, 15,  7,
    };

    /**
     * Tabulka pro závěrečnou permutaci.
     *
     * @see BitUtils#permutate(long, byte[])
     */
    final static byte[] FP = {
            40,  8, 48, 16, 56, 24, 64, 32,
            39,  7, 47, 15, 55, 23, 63, 31,
            38,  6, 46, 14, 54, 22, 62, 30,
            37,  5, 45, 13, 53, 21, 61, 29,
            36,  4, 44, 12, 52, 20, 60, 28,
            35,  3, 43, 11, 51, 19, 59, 27,
            34,  2, 42, 10, 50, 18, 58, 26,
            33,  1, 41,  9, 49, 17, 57, 25,
    };

    /**
     * Zašifruje nebo dešifruje blok pomocí DES.
     *
     * @param block 64 bitový blok
     * @param keys 16 klíčů
     * @return zašifrovaný/dešifrovaný blok
     */
    static long apply(long block, final long[] keys) {
        assert keys.length == 16;

        // počáteční permutace
        block = BitUtils.permutate(block, IP);

        // 16 iterací
        //   L' = R
        //   R' = L xor F(R,K)

        int leftHalfBlock = (int) (block >>> 32);
        int rightHalfBlock = (int) block;

        for (int i = 0; i < 16; i++) {
            final int temp = leftHalfBlock ^ computeF(rightHalfBlock, keys[i]);
            leftHalfBlock = rightHalfBlock;
            rightHalfBlock = temp;
        }

        // sloučení levé a pravé půlky
        // strany se při tom ještě prohodí
        block = (rightHalfBlock & 0xFFFFFFFFL) << 32 | (leftHalfBlock & 0xFFFFFFFFL);
        // pozn.: (long) rightHalfBlock se chová jinak pro záporné hodnoty, proto maska

        // závěrečná permutace
        return BitUtils.permutate(block, FP);
    }


    // ------ implementace funkce F ------

    /**
     * Tabulka hodnot pro expanzní funkci.
     *
     * @see BitUtils#expand(int, byte[])
     */
    static final byte[] E = {
            32,  1,  2,  3,  4,  5,
            4,  5,  6,  7,  8,  9,
            8,  9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32,  1,
    };

    /**
     * Substituční tabulky.
     */
    final static byte[][][] S = {
            // S1
            {
                    { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
                    { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
                    { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
                    { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 },
            },
            // S2
            {
                    { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
                    { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
                    { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
                    { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 },
            },
            // S3
            {
                    { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
                    { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
                    { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
                    { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 },
            },
            // S4
            {
                    { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
                    { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
                    { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
                    { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 },
            },
            // S5
            {
                    { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
                    { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
                    { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
                    { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 },
            },
            // S6
            {
                    { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
                    { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
                    { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
                    { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 },
            },
            // S7
            {
                    { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
                    { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
                    { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
                    { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 },
            },
            // S8
            {
                    { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
                    { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
                    { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
                    { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11},
            }
    };

    /**
     * Tabulka hodnot pro závěrečnou permutaci.
     *
     * @see BitUtils#permutate(int, byte[])
     */
    final static byte[] P = {
            16,  7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26,  5, 18, 31, 10,
            2,  8, 24, 14, 32, 27,  3,  9,
            19, 13, 30,  6, 22, 11,  4, 25,
    };

    /**
     * Spočte funkci "F".
     *
     * @param rightHalfBlock pravá část bloku
     * @param key klíč
     * @return výsledek
     */
    static int computeF(final int rightHalfBlock, final long key) {
        // E(R) xor K (48 bitů, zarovnáno doprava)
        final long temp = BitUtils.expand(rightHalfBlock, E) ^ key;

        // mezivýsledek se rozdělí na 8 částí po 6 bitech
        // na tyto části se použije substituční tabulka
        // na část nejvíce v levo se použije substituční tabulka S1 atd...
        // výsledky se sloučí do jednoho bloku

        int outputHalfBlock = 0;
        for (int i = 0; i < 8; i++) {
            final int next6bits = ((int) (temp >>> 6 * (7 - i))) & 0b111111;

            // první a poslední bit dají číslo 0-3 (řádek v tabulce)
            final int row = ((next6bits & 0b100000) >>> 4) | next6bits & 0b000001;

            // prostřední bity dají číslo 0-15 (sloupce v tabulce)
            final int column = (next6bits & 0b011110) >>> 1;

            outputHalfBlock = (outputHalfBlock << 4) | S[i][row][column];
        }

        // výstupní permutace
        return BitUtils.permutate(outputHalfBlock, P);
    }

}
