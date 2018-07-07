package cz.harag.bit.sp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.IntStream;

/**
 * Poskytuje metody pro šifrování a dešifrování vstupních proudů InputStream
 * pomocí DES.
 *
 * @version 2018-03-31
 * @author Patrik Harag
 */
public class DES {

    private DES() {
        // skrytý konstruktor
    }

    private static final int CHUNK_SIZE = 256; // × 64 bytů

    /**
     * Zašifruje vstupní proud pomocí DES v módu Electronic Codebook (ECB).
     *
     * @param in vstupní proud
     * @param out výstupní proud
     * @param key klíč
     */
    public static void encryptECB(InputStream in, OutputStream out, long key) throws IOException {
        long[] keys = DESKeyGenerator.generateKeysForEncryption(key);

        LongInputStream longInputStream = new LongInputStreamPlainText(in, (byte) 0);
        LongOutputStream longOutputStream = new LongOutputStream(out);
        longOutputStream.writeLong(in.available());

        applyECB(longInputStream, longOutputStream, keys);
        longOutputStream.flush();
    }

    /**
     * Dešífruje vstupní proud pomocí DES v módu Electronic Codebook (ECB).
     *
     * @param in vstupní proud
     * @param out výstupní proud
     * @param key klíč
     */
    public static void decryptECB(InputStream in, OutputStream out, long key) throws IOException {
        long[] keys = DESKeyGenerator.generateKeysForDecryption(key);

        LongInputStream longInputStream = new LongInputStreamCipherText(in, (byte) 0);
        LongOutputStream longOutputStream = new LongOutputStream(out);

        applyECB(longInputStream, longOutputStream, keys);
        longOutputStream.flush();
    }

    private static void applyECB(
            LongInputStream inputStream, LongOutputStream outputStream, long[] keys)
            throws IOException {

        final int available = inputStream.available();
        for (int i = 0; i < available; i++) {
            long block = inputStream.readLong();
            long result = DESCore.apply(block, keys);
            outputStream.writeLong(result, inputStream.getLastBlockPadding());
        }
    }

    /**
     * Zašifruje vstupní proud pomocí DES v módu Electronic Codebook (ECB).
     *
     * @param in vstupní proud
     * @param out výstupní proud
     * @param key klíč
     */
    public static void encryptParallelECB(InputStream in, OutputStream out, long key) throws IOException {
        long[] keys = DESKeyGenerator.generateKeysForEncryption(key);

        LongInputStream longInputStream = new LongInputStreamPlainText(in, (byte) 0);
        LongOutputStream longOutputStream = new LongOutputStream(out);
        longOutputStream.writeLong(in.available());

        applyParallelECB(longInputStream, longOutputStream, keys);
        longOutputStream.flush();
    }

    /**
     * Dešífruje vstupní proud pomocí DES v módu Electronic Codebook (ECB).
     *
     * @param in vstupní proud
     * @param out výstupní proud
     * @param key klíč
     */
    public static void decryptParallelECB(InputStream in, OutputStream out, long key) throws IOException {
        long[] keys = DESKeyGenerator.generateKeysForDecryption(key);

        LongInputStream longInputStream = new LongInputStreamCipherText(in, (byte) 0);
        LongOutputStream longOutputStream = new LongOutputStream(out);

        applyParallelECB(longInputStream, longOutputStream, keys);
        longOutputStream.flush();
    }

    private static void applyParallelECB(
            LongInputStream inputStream, LongOutputStream outputStream, long[] keys)
            throws IOException {

        int available = inputStream.available();
        long[] chunk = new long[Math.min(CHUNK_SIZE, available)];
        while (available > 0) {
            int nextChunkSize = Math.min(CHUNK_SIZE, available);
            for (int i = 0; i < nextChunkSize; i++) {
                chunk[i] = inputStream.readLong();
                available--;
            }

            IntStream.range(0, nextChunkSize).parallel().forEach(i -> {
                chunk[i] = DESCore.apply(chunk[i], keys);
            });

            for (int i = 0; i < nextChunkSize; i++) {
                if (i == nextChunkSize - 1) {
                    outputStream.writeLong(chunk[i], inputStream.getLastBlockPadding());
                } else {
                    outputStream.writeLong(chunk[i]);
                }
            }
        }
    }

    /**
     * Zašifruje vstupní proud pomocí DES v módu Cipher Block Chaining (CBC).
     *
     * @param in vstupní proud
     * @param out výstupní proud
     * @param key klíč
     */
    public static void encryptCBC(InputStream in, OutputStream out, long key, long iv) throws IOException {
        long[] keys = DESKeyGenerator.generateKeysForEncryption(key);

        LongInputStream longInputStream = new LongInputStreamPlainText(in, (byte) 0);
        LongOutputStream longOutputStream = new LongOutputStream(out);
        longOutputStream.writeLong(in.available());

        long encrypted = iv;
        int available = longInputStream.available();
        while (available-- > 0) {
            long block = longInputStream.readLong();
            encrypted = DESCore.apply(block ^ encrypted, keys);
            longOutputStream.writeLong(encrypted);
        }
        longOutputStream.flush();
    }

    /**
     * Dešífruje vstupní proud pomocí DES v módu Cipher Block Chaining (CBC).
     *
     * @param in vstupní proud
     * @param out výstupní proud
     * @param key klíč
     */
    public static void decryptCBC(InputStream in, OutputStream out, long key, long iv) throws IOException {
        long[] keys = DESKeyGenerator.generateKeysForDecryption(key);

        LongInputStream longInputStream = new LongInputStreamCipherText(in, (byte) 0);
        LongOutputStream longOutputStream = new LongOutputStream(out);

        long lastBlock = iv;
        int available = longInputStream.available();
        while (available-- > 0) {
            long block = longInputStream.readLong();
            long result = DESCore.apply(block, keys);
            longOutputStream.writeLong(result ^ lastBlock, longInputStream.getLastBlockPadding());
            lastBlock = block;
        }
        longOutputStream.flush();
    }

}
