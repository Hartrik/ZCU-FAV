package cz.hartrik.ppa1.sort;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Třída sloužící ke generování pseudo náhodných čísel.
 *
 * @version 2015-12-05
 * @author Patrik Harag
 */
public class Rnd implements Iterable<Integer> {

    private final int seed;

    /**
     * Vytvoří novou instanci.
     *
     * @param seed počáteční seed, dvouciferné číslo
     * @throws IllegalArgumentException seed není dvouciferné číslo
     */
    public Rnd(int seed) {
        if (seed < 10 || seed > 99)
            throw new IllegalArgumentException();

        this.seed = seed;
    }

    private IntStream create() {
        IntStream.Builder builder = IntStream.builder().add(seed);
        Set<Integer> used = new LinkedHashSet<>();
        used.add(seed);

        int temp = seed;
        while (!used.contains(temp = countNext(temp))) {
            builder.accept(temp);
            used.add(temp);
        }

        return builder.build();
    }

    private int countNext(int last) {
        return last * last / (last > 31 ? 100 : 10) + 1;

//        String firstTwoDigits = String.valueOf(last * last).substring(0, 2);
//        return Integer.valueOf(firstTwoDigits) + 1;
    }

    /**
     * Vytvoří iterátor generující náhodná čísla podle zadaného seedu.
     *
     * @return iterátor
     */
    @Override
    public Iterator<Integer> iterator() {
        return create().iterator();
    }

    /**
     * Vrátí proud generující náhodná čísla podle zadaného seedu.
     * Při každém zavolání metody bude vytvořen nový proud.
     *
     * @return proud čísel
     */
    public IntStream intStream() {
        return create();
    }

    /**
     * Vrátí pole náhodných čísel.
     *
     * @return pole
     */
    public int[] toArray() {
        return create().toArray();
    }

    /**
     * Vrátí seed.
     *
     * @return seed
     */
    public int getSeed() {
        return seed;
    }

}