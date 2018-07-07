package cz.harag.bit.sp;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream pro čtení čísel datového typu long.
 *
 * @author Patrik Harag
 * @version 2018-03-31
 */
class LongInputStreamPlainText extends LongInputStream {

    LongInputStreamPlainText(InputStream inputStream, byte paddingByte)
            throws IOException {

        super(inputStream, paddingByte);
    }

    @Override
    int getLastBlockPadding() {
        return 0;
    }

}
