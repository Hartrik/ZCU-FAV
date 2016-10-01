package p1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Vstupní třída
 *
 * @version 2016-04-17
 * @author Patrik Harag
 */
public class Main {

    public static void main(String[] arrstring) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("dataObchodniDum.txt"));
        Heap heap = new Heap(32);

        // načtení dodavatelů
        while (sc.hasNextInt()) {
            heap.add(sc.nextInt(), sc.nextInt(), sc.nextInt());
        }

        System.out.println();
        System.out.println("Výpis struktury - výchozí stav:");
        heap.forEach(System.out::println);

        // vykonání instrukcí
        while (sc.hasNext("\\?")) {
            sc.next();  // zahození otazníku...

            Item min = heap.findCheapest(sc.nextInt());

            System.out.println("");
            System.out.println("Nalezeno:");
            System.out.println((min == null)
                    ? "Požadavky nikdo nebyl schopen splnit" : min);
            System.out.println("");

            // přidání nového dodavatele
            if (sc.hasNextInt()) {
                heap.add(sc.nextInt(), sc.nextInt(), sc.nextInt());
            }

            System.out.println("Výpis struktury - po vykonání instrukce:");
            heap.forEach(System.out::println);
        }
    }

}