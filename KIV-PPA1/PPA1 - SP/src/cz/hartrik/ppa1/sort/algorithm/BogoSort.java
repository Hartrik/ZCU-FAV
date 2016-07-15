package cz.hartrik.ppa1.sort.algorithm;

import java.util.Iterator;
import java.util.Random;

/**
 * Implementace řadícího algoritmu BogoSort.
 *
 * @version 2015-10-22
 * @author Patrik Harag
 * @param <T> typ dat
 */
public class BogoSort<T extends Comparable<T>> extends AlgorithmBase<T> {

    private final Random r = new Random();

    /**
     * Vytvoří novou instanci.
     *
     * @param data data, která se budou řadit
     */
    public BogoSort(Data<T> data) {
        super(data);
    }

    @Override
    public void nextStep() {
        if (data.size() > 1)
            data.swap(r.nextInt(data.size()), r.nextInt(data.size()));
    }

    @Override
    public boolean isSorted() {
        Iterator<T> iter = data.list().iterator();
        if (!iter.hasNext()) return true;

        T o1 = iter.next();
        while (iter.hasNext()) {
            T o2 = iter.next();
            if (o1.compareTo(o2) > 0)
                return false;

            o1 = o2;
        }

        return true;
    }

}