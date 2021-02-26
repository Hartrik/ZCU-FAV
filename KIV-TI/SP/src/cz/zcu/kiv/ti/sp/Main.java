package cz.zcu.kiv.ti.sp;

import cz.zcu.kiv.ti.sp.huffman.HuffmanTree;
import cz.zcu.kiv.ti.sp.huffman.Item;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * @version 2016-11-05
 * @author Patrik Harag
 * @author Jakub Vasta
 */
public class Main {

    private static String input_filename = null;
    private static String output_filename = null;
    private static String decoded_filename = null;

    public static boolean info = false;

    public static final int CHARSET_SIZE = 256;

    public static void main(String[] args) throws IOException {
        // vstupní soubor

       switch (args.length) {
           case 4 : if (args[0].equalsIgnoreCase("-v")) {
                        info = true;
                    }
                    else {
                        System.out.println("Invalid input");
                        return;
                    }

                    switch(args[1]) {
                        case "-c" : input_filename = args[2];
                                    output_filename = args[3];
                                    code();
                                    info();
                                    break;
                        case "-d" : output_filename = args[2];
                                    decoded_filename = args[3];
                                    decode();
                                    break;
                        default   : System.out.println("Invalid input");
                                    break;
                    }
                    break;
           case 3 : switch(args[0]) {
                        case "-c" : input_filename = args[1];
                                    output_filename = args[2];
                                    code();
                                    info();
                                    break;
                        case "-d" : output_filename = args[1];
                                    decoded_filename = args[2];
                                    decode();
                                    break;
                        default   : System.out.println("Invalid input");
                                    break;
                    }
                    break;
           default: System.out.println("Invalid input");
                    break;
       }
    }

    // Metoda na zakódování
    private static void code() throws IOException{
        Path file = Paths.get(input_filename);

        StopWatch.measureAndPrint("%nTotal time: %d ms%n", () -> {
            int[] frequencies = createOccurrenceTable(file);
            HuffmanTree tree = HuffmanTree.create(frequencies);

            // výpis tabulky
            if (info) {
               printTable(tree);
            }

            String[] table = tree.asEncodingTable();

            IOUtils.encode(file, Paths.get(output_filename), table, frequencies);
        });
    }

    // Metoda na dekódování
    private static void decode() throws IOException{
        StopWatch.measureAndPrint("%nTotal time: %d ms%n", () -> {
            IOUtils.decode(Paths.get(output_filename), Paths.get(decoded_filename));
        });
    }

    // Metoda vypisující informace o kompresi
    private static void info() {
        File f = new File(input_filename);
        File g = new File(output_filename);

        int ifd = 4 + CHARSET_SIZE * 4; //int na zapsání velikosti tabulky + int na každou frekvenci
        System.out.printf("Original file:    %d [byte]%n", f.length());
        System.out.printf("Compressed file:  %d (header: %d, body: %d) [byte]%n",
                g.length(), ifd, (g.length() - ifd));
        System.out.printf("Compression rate: %.2f%n",
                (double) g.length() / f.length() * 100);
    }

    public static int[] createOccurrenceTable(Path file) throws IOException {
        int[] table = new int[CHARSET_SIZE];
        // int by měl stačit pro maximální velikost souboru < 2 GiB

        IOUtils.forEachByte(file, v -> table[v]++);
        return table;
    }

    // Vypisuje tabulku
    public static void printTable(HuffmanTree tree) {
        System.out.println("Tabulka");
        System.out.println("SYMBOL\tČETNOST\tKÓD");
        for (Item i : tree.asList()) {
            System.out.printf("%03d\t%d\t%s%n",
            i.getValue(), i.getFrequency(), i.getCode());
        }
    }
}