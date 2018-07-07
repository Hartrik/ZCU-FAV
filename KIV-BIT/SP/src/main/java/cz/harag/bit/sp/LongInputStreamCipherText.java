package cz.harag.bit.sp;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream pro čtení čísel datového typu long.
 *
 * @author Patrik Harag
 * @version 2018-03-31
 */
class LongInputStreamCipherText extends LongInputStream {

    private final long lastBlockPosition;
    private final int lastBlockPadding;

    LongInputStreamCipherText(InputStream inputStream, byte paddingByte)
            throws IOException {

        super(inputStream, paddingByte);

        long plainTextSize = readLong();
        this.lastBlockPadding = (int) (plainTextSize % 8 != 0
                ? 8 - (plainTextSize % 8)
                : 0);
        this.lastBlockPosition = plainTextSize + lastBlockPadding;
    }

    @Override
    int getLastBlockPadding() {
        if (lastBlockPadding == 0) {
            return 0;
        } else {
            if (bytePosition > lastBlockPosition) {
                return lastBlockPadding;
            }
            return 0;
        }
    }

}
