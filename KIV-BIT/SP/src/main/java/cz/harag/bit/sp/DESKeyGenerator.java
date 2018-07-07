package cz.harag.bit.sp;

/**
 * Třída pro generování klíčů.
 * Tento algoritmus je uveden v příloze specifikace DES:
 * https://csrc.nist.gov/csrc/media/publications/fips/46/3/archive/1999-10-25/documents/fips46-3.pdf
 *
 * @author Patrik Harag
 * @version 2018-03-30
 */
public class DESKeyGenerator {

    private DESKeyGenerator() {
        // skrytý konstruktor
    }

    /**
     * Tabulka pro vstupní permutaci.
     */
    final static byte[] PC_1 = {
            57, 49, 41, 33, 25, 17,  9,
             1, 58, 50, 42, 34, 26, 18,
            10,  2, 59, 51, 43, 35, 27,
            19, 11,  3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
             7, 62, 54, 46, 38, 30, 22,
            14,  6, 61, 53, 45, 37, 29,
            21, 13,  5, 28, 20, 12,  4,
    };

    /**
     * Tabulka pro výstupní permataci.
     */
    final static byte[] PC_2 = {
            14, 17, 11, 24,  1,  5,
             3, 28, 15,  6, 21, 10,
            23, 19, 12,  4, 26,  8,
            16,  7, 27, 20, 13,  2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32,
    };

    /**
     * Počet cyklických posunů pro jednotlivé kroky algoritmu DES.
     */
    final static byte[] SHIFTS = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    /**
     * Vygeneruje 16 klíčů o 48 bitech z jednoho 64 bitového klíče.
     *
     * @param key klíč
     * @return pole 16 klíčů
     */
    static long[] generateKeysForEncryption(final long key) {
        final long permutatedKey = BitUtils.permutate(key, PC_1);
        // vytvoří 56 bitů dlouhý klíč

        int c = (int) (permutatedKey >>> 28);
        int d = (int) (permutatedKey & 0x0FFFFFFF);

        final long[] keys = new long[16];
        for (int i = 0; i < 16; i++) {
            c = BitUtils.circularLeftShift(c, 28, SHIFTS[i]);
            d = BitUtils.circularLeftShift(d, 28, SHIFTS[i]);

            final long choice = ((c & 0xFFFFFFFFL) << 28) | (d & 0xFFFFFFFFL);
            keys[i] = BitUtils.permutate(choice, 56, PC_2);
        }
        return keys;
    }

    /**
     * Vygeneruje 16 klíčů o 48 bitech z jednoho 64 bitového klíče.
     *
     * @param key klíč
     * @return pole 16 klíčů
     */
    static long[] generateKeysForDecryption(final long key) {
        long[] keysForEncryption = generateKeysForEncryption(key);
        long[] keysForDecryption = new long[16];
        for (int i = 0; i < 16; i++) {
            keysForDecryption[15 - i] = keysForEncryption[i];
        }
        return keysForDecryption;
    }

}
