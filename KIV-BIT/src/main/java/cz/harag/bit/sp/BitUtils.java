package cz.harag.bit.sp;

/**
 * Pomocné metody pro práci s bloky.
 *
 * @author Patrik Harag
 * @version 2018-03-30
 */
class BitUtils {

    /**
     * Provede permutaci bloku podle tabulky.
     *
     * @param inputBlock vstupní blok
     * @param table tabulka permutací
     * @return permutovaný blok
     */
    static long permutate(final long inputBlock, byte[] table) {
        return permutate(inputBlock, 64, table);
    }

    /**
     * Provede permutaci bloku podle tabulky.
     *
     * @param inputBlock vstupní blok
     * @param blockSize velikost bloku
     * @param table tabulka permutací
     * @return permutovaný blok
     */
    static long permutate(final long inputBlock, int blockSize, byte[] table) {
        assert blockSize > 0 && blockSize <= 64;

        long outputBlock = 0;
        for (final byte position : table) {
            // vezme bit na dané pozici
            final long bit = (inputBlock >>> (blockSize - position)) & 1;

            // připojí bit na konec
            outputBlock = (outputBlock << 1) | bit;
        }
        return outputBlock;
    }

    /**
     * Provede permutaci půl-bloku podle tabulky.
     *
     * @param inputHalfBlock vstupní půl-blok
     * @param table tabulka permutací
     * @return permutovaný půl-blok
     */
    static int permutate(final int inputHalfBlock, byte[] table) {
        return permutate(inputHalfBlock, 32, table);
    }

    /**
     * Provede permutaci půl-bloku podle tabulky.
     *
     * @param inputHalfBlock vstupní půl-blok
     * @param blockSize velikost bloku
     * @param table tabulka permutací
     * @return permutovaný půl-blok
     */
    static int permutate(final int inputHalfBlock, int blockSize, byte[] table) {
        assert blockSize > 0 && blockSize <= 32;

        int outputBlock = 0;
        for (final byte position : table) {
            // vezme bit na dané pozici
            final int bit = (inputHalfBlock >>> (blockSize - position)) & 1;

            // připojí bit na konec
            outputBlock = (outputBlock << 1) | bit;
        }
        return outputBlock;
    }

    /**
     * Provede exmanzi půl-bloku podle tabulky.
     *
     * @param inputHalfBlock vstupní blok
     * @param table tabulka
     * @return expandovaný blok
     */
    static long expand(final int inputHalfBlock, byte[] table) {
        long outputBlock = 0;
        for (final byte position : table) {
            // vezme bit na dané pozici
            final int bit = (inputHalfBlock >>> (32 - position)) & 1;

            // připojí bit na konec
            outputBlock = (outputBlock << 1) | bit;
        }
        return outputBlock;
    }

    /**
     * Provede cyklický posun v levo.
     *
     * @param block blok
     * @param shift posun
     * @return cyklicky posunutý blok
     */
    static int circularLeftShift(int block, int shift) {
        return (block << shift) | (block >>> (32 - shift));
    }

    /**
     * Provede cyklický posun v levo.
     *
     * @param block blok
     * @param blockSize velikost bloku
     * @param shift posun
     * @return cyklicky posunutý blok
     */
    static int circularLeftShift(int block, int blockSize, int shift) {
        int mask = 0xFFFFFFFF >>> (32 - blockSize);
        return ((block << shift) & mask) | (block >>> (blockSize - shift));
    }

}
