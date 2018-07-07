package cz.harag.bit.c02;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

/**
 *
 * @author Patrik Harag
 */
public class SteganographyTest {

    @Test
    public void test_1() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/c02/image.bmp"));

        for (int offset = 0; offset < 8; offset++) {
            System.out.print(offset + " | ");

            int bufferLength = 0;
            int buffer = 0;

            for (int i = offset; i < bytes.length; i++) {
                byte b = bytes[i];

                buffer = buffer << 1;
                buffer = buffer | (b & 1);
                bufferLength++;

                if (bufferLength == 8) {
                    char c = (char) buffer;
                    System.out.print(c);

                    if (c == '\n') {
                        break;
                    }

                    buffer = 0;
                    bufferLength = 0;
                }
            }
        }
    }

    @Test
    public void test_2() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/c02/image.bmp"));

        int offset = bytes[10];
        int bufferLength = 0;
        int buffer = 0;

        for (int i = offset; i < bytes.length; i++) {
            byte b = bytes[i];

            buffer = buffer << 1;
            buffer = buffer | (b & 1);
            bufferLength++;

            if (bufferLength == 8) {
                char c = (char) buffer;
                System.out.print(c);

                if (c == '\n') {
                    break;
                }

                buffer = 0;
                bufferLength = 0;
            }
        }
    }

}
