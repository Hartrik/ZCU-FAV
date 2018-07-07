package cz.harag.bit.sp

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import java.nio.charset.StandardCharsets

/**
 *
 * @version 2018-03-31
 * @author Patrik Harag
 */
@RunWith(Parameterized.class)
class DESTest {

    @Parameterized.Parameters(name = "{0}")
    static Collection<Object[]> data() {
        [
            [
                "ECB",
                { byte[] data, long key ->
                    def encrypted = new ByteArrayOutputStream()
                    DES.encryptECB(new ByteArrayInputStream(data), encrypted, key)
                    def decrypted = new  ByteArrayOutputStream()
                    DES.decryptECB(new ByteArrayInputStream(encrypted.toByteArray()), decrypted, key)
                    return decrypted.toByteArray()
                }
            ],
            [
                "Parallel ECB",
                { byte[] data, long key ->
                    def encrypted = new ByteArrayOutputStream()
                    DES.encryptParallelECB(new ByteArrayInputStream(data), encrypted, key)
                    def decrypted = new  ByteArrayOutputStream()
                    DES.decryptParallelECB(new ByteArrayInputStream(encrypted.toByteArray()), decrypted, key)
                    return decrypted.toByteArray()
                }
            ],
            [
                "CBC",
                { byte[] data, long key ->
                    long iv = 86846944164L
                    def encrypted = new ByteArrayOutputStream()
                    DES.encryptCBC(new ByteArrayInputStream(data), encrypted, key, iv)
                    def decrypted = new  ByteArrayOutputStream()
                    DES.decryptCBC(new ByteArrayInputStream(encrypted.toByteArray()), decrypted, key, iv)
                    return decrypted.toByteArray()
                }
            ]
        ]*.toArray()
    }

    private String modeName
    private Closure<byte[]> encryptDecrypt

    DESTest(String modeName, Closure<byte[]> encryptDecrypt) {
        this.modeName = modeName
        this.encryptDecrypt = encryptDecrypt
    }

    @Test
    void testEmpty() {
        byte[] data = []
        long key = 3489678634516L
        assert data == encryptDecrypt(data, key)
    }

    @Test
    void testOneBlock() {
        byte[] data = [1, 2, 3, 4, 5, 6, 7, 8]
        long key = 3489678634516L

        assert data == encryptDecrypt(data, key)
    }

    @Test
    void testOneBlockShort() {
        byte[] data = [1, 2, 3]
        long key = 3489678634516L

        assert data == encryptDecrypt(data, key)
    }

    @Test
    void testLong() {
        String text = """
            Lorem ipsum dolor sit amet, consectetuer adipiscing elit. In convallis. 
            Duis risus. Class aptent taciti sociosqu ad litora torquent per conubia 
            nostra, per inceptos hymenaeos. Mauris dictum facilisis augue.
        """
        byte[] data = text.stripIndent().getBytes(StandardCharsets.UTF_8)

        long key = 3489678634516L

        assert data == encryptDecrypt(data, key)
    }

    @Test
    void testGenerated() {
        long key = 3489678634516L

        for (int i = 0; i < 1024; i++) {
            byte[] data = new byte[i]
            for (int j = 0; j < i; j++) {
                data[j] = j
            }
            assert data == encryptDecrypt(data, key)
        }
    }

}
