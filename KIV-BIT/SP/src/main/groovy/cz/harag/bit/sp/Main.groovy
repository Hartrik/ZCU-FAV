package cz.harag.bit.sp

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Vstupní třída.
 *
 * @version 2018-03-31
 * @author Patrik Harag
 */
class Main {

    static enum Mode {
        ECB,
        PARALLEL_ECB,
        CBC
    }

    /**
     * Vstupní metoda.
     *
     * @param args
     */
    static void main(String... args) {
        try {
            process(args)
        } catch (IOException | UncheckedIOException e) {
            println("An I/O error occurred: " + e.getMessage())
        } catch (IllegalArgumentException e) {
            println("Illegal argument: " + e.getMessage())
        }
    }

    private static void process(String... args) {
        Path inputFile
        Path outputFile
        boolean encrypt = true
        Long key
        long iv = 0
        Mode mode = Mode.ECB

        def iterator = args.iterator()
        while (iterator.hasNext()) {
            String arg = iterator.next()

            switch (arg.toLowerCase()) {
                case '-in':
                    inputFile = Paths.get(getNextArgument(iterator, arg))
                    break
                case '-out':
                    outputFile = Paths.get(getNextArgument(iterator, arg))
                    break
                case '-e':
                case '-encrypt':
                    encrypt = true
                    break
                case '-d':
                case '-decrypt':
                    encrypt = false
                    break
                case '-k':
                case '-key':
                    key = getNextArgumentLongHex(iterator, arg)
                    break
                case '-ecb':
                    mode = Mode.ECB
                    break
                case '-pecb':
                case '-parallel-ecb':
                    mode = Mode.PARALLEL_ECB
                    break
                case '-cbc':
                    mode = Mode.CBC
                    break
                case '-iv':
                    iv =  getNextArgumentLongHex(iterator, arg)
                    break
                default:
                    println "Unknown key: '$arg'"
            }
        }

        if (inputFile == null)
            throw new IllegalArgumentException("Input file not set!")

        if (outputFile == null)
            throw new IllegalArgumentException("Output file not set!")

        if (key == null)
            throw new IllegalArgumentException("Key not set!")

        println "${ encrypt ? "Encryption" : "Decryption" } started with mode: $mode"

        inputFile.newInputStream().with { input ->
            outputFile.newOutputStream().with { output ->
                final long start = System.currentTimeMillis()
                if (mode == Mode.ECB) {
                    if (encrypt)
                        DES.encryptECB(input, output, key)
                    else
                        DES.decryptECB(input, output, key)
                } else if (mode == Mode.PARALLEL_ECB) {
                    if (encrypt)
                        DES.encryptParallelECB(input, output, key)
                    else
                        DES.decryptParallelECB(input, output, key)
                } else if (mode == Mode.CBC) {
                    if (encrypt)
                        DES.encryptCBC(input, output, key, iv)
                    else
                        DES.decryptCBC(input, output, key, iv)
                } else {
                    throw new AssertionError('Unsupported mode')
                }

                final long end = System.currentTimeMillis()
                println "${encrypt ? "Encryption" : "Decryption"} done after ${end - start} ms"
            }
        }
    }

    private static long getNextArgumentLongHex(Iterator<String> iterator, String key) {
        try {
            Long.parseLong(getNextArgument(iterator, key), 16)
        } catch (e) {
            throw new IllegalArgumentException("Wrong format of value for key '$key'")
        }
    }

    private static getNextArgument(Iterator<String> iterator, String key) {
        if (iterator.hasNext())
            return iterator.next()
        else
            throw new IllegalArgumentException("Value for key '$key' not set")
    }

}
