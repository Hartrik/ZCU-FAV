package cz.harag.bit.sp;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream pro čtení čísel datového typu long.
 *
 * @author Patrik Harag
 * @version 2018-03-31
 */
abstract class LongInputStream {

    private final InputStream inputStream;
    private final byte paddingChar;
    private final int padding;

    private byte[] buffer = new byte[8];
    int bytePosition = 0;

    LongInputStream(InputStream inputStream, byte paddingByte) throws IOException {
        this.inputStream = inputStream;
        this.paddingChar = paddingByte;

        this.padding = (inputStream.available() % 8 != 0)
                ? 8 - (inputStream.available() % 8)
                : 0;
    }

    long readLong() throws IOException {
        int read = inputStream.read(buffer);
        if (read != 8) {
            for (int i = 0; i < 8 - read; i++) {
                buffer[7 - i] = paddingChar;
            }
        }
        bytePosition += read;

        return ((buffer[0] & 255L) << 56)
                + ((buffer[1] & 255L) << 48)
                + ((buffer[2] & 255L) << 40)
                + ((buffer[3] & 255L) << 32)
                + ((buffer[4] & 255L) << 24)
                + ((buffer[5] & 255L) << 16)
                + ((buffer[6] & 255L) << 8)
                + (buffer[7] & 255L);
    }

    abstract int getLastBlockPadding();

    int available() throws IOException {
        return (inputStream.available() + padding) / 8;
    }

}
