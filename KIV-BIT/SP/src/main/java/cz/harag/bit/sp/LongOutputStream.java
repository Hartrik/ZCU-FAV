package cz.harag.bit.sp;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Stream pro zápis čísel typu long.
 *
 * @author Patrik Harag
 * @version 2018-03-31
 */
class LongOutputStream {

    private final OutputStream outputStream;

    LongOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    final void writeLong(long block, int padding) throws IOException {
        if (padding == 0) {
            writeLong(block);
        } else {
            for (int i = 0; i < 8 - padding; i++) {
                outputStream.write((byte) (block >>> (8 * (7 - i))));
            }
        }
    }

    private byte writeBuffer[] = new byte[8];
    final void writeLong(long v) throws IOException {
        writeBuffer[0] = (byte)(v >>> 56);
        writeBuffer[1] = (byte)(v >>> 48);
        writeBuffer[2] = (byte)(v >>> 40);
        writeBuffer[3] = (byte)(v >>> 32);
        writeBuffer[4] = (byte)(v >>> 24);
        writeBuffer[5] = (byte)(v >>> 16);
        writeBuffer[6] = (byte)(v >>>  8);
        writeBuffer[7] = (byte)(v >>>  0);
        outputStream.write(writeBuffer, 0, 8);
    }

    void flush() throws IOException {
        outputStream.flush();
    }
}
