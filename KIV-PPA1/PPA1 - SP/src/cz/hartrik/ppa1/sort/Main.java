package cz.hartrik.ppa1.sort;

import cz.hartrik.ppa1.sort.app.App;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Třída, která zpracovává všechny předepsané vstupy a výstupy.
 * Nestará se o grafický výstup.
 *
 * @version 2015-12-05
 * @author Patrik Harag
 */
public class Main {

    private static final String OUT_FILE = "vystup.txt";
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Vstupní metoda.
     *
     * @param args argumenty z příkazové řádky
     */
    public static void main(String... args) {
//      args = new String[] { "98" };  // test

        try {
            if (args.length != 0) {
                processArgs(args);

                App.main(args);

            } else {
                System.out.print("Zadejte nazev souboru: ");
                String fileName = scanner.nextLine();

                System.out.println("---Vysledky---");
                processFile(Paths.get(fileName));
            }

        } catch (IOException ex) {
            ex.printStackTrace();  // kvůli zadání...
        }
    }

    /**
     * Zpracuje argumenty z příkazové řádky.
     *
     * @param args argumenty z příkazové řádky
     * @throws IOException chyba při zapisování do souboru
     */
    private static void processArgs(String... args) throws IOException {
        int[] seeds = Stream.of(args).mapToInt(Integer::parseInt).toArray();

        try (FileOutputStream fos = new FileOutputStream(OUT_FILE, false);
                PrintStream ps = new PrintStream(fos, true, "UTF-8")) {

            printSequences(ps, seeds);
        }
    }

    /**
     * Zpracuje vstupní soubor.
     *
     * @param path soubor se vstupem
     * @throws IOException chyba při čtení ze souboru
     */
    private static void processFile(Path path) throws IOException {
        int[] seeds = Files.lines(path).mapToInt(Integer::parseInt).toArray();
        printSequences(System.out, seeds);
    }

    /**
     * Z předaných počátečních hodnot vytvoří sekvence a ty v požadovaném
     * formátu vytiskne na výstupu.
     *
     * @param out výstup
     * @param seeds počáteční hodnoty
     */
    private static void printSequences(PrintStream out, int... seeds) {
        IntStream.of(seeds)
                .mapToObj(Rnd::new)
                .forEach(rnd -> printSequnce(out, rnd));
    }

    /**
     * Vezme sekvenci náhodných čísel a vytiskne ji do proudu v požadovaném
     * formátu.
     *
     * @param rnd sekvence náhodných čísel
     * @param out výstup
     */
    private static void printSequnce(PrintStream out, Rnd rnd) {
        int[] original = rnd.toArray();
        int[] sorted = rnd.intStream().sorted().toArray();

        out.println(original.length + " " + Arrays.toString(original));
        out.println(sorted.length   + " " + Arrays.toString(sorted));
        out.println();
    }

    // ---- ---- ----

    /**
     * Seřadí pole metodou SelectSort.
     *
     * @param pole řazené pole
     */
    private static void selectSort(int[] pole) {
        for (int i = 0; i < pole.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < pole.length; j++) {
                if (pole[j] < pole[minIndex]) {
                    minIndex = j;
                }
            }
            if (i != minIndex) {
                int pom = pole[minIndex];
                pole[minIndex] = pole[i];
                pole[i] = pom;
            }
        }
    }

}