package cz.hartrik.ppa1.sort.algorithm;

/**
 * Implementace řadícího algoritmu Selection Sort.
 *
 * @version 2015-10-25
 * @author Patrik Harag
 * @param <T> typ tříděných dat
 */
public class SelectionSort<T extends Comparable<T>> extends AlgorithmBase<T> {

    private boolean sorted;
    private int i;

    /**
     * Vytvoří novou instanci.
     *
     * @param data data, která se budou řadit
     */
    public SelectionSort(Data<T> data) {
        super(data);
    }

    @Override
    public void nextStep() {
        if (isSorted())
            return;

        if (i < data.size() - 1) {

            // nalezení maxima v neseřazených prvcích
            T max = data.list().get(0);
            int maxIndex = 0;

            for (int j = 1; j < data.size() - i; j++) {
                T next = data.list().get(j);
                if (next.compareTo(max) == 1) {
                    max = next;
                    maxIndex = j;
                }
            }

            data.swap(maxIndex, data.size() - i - 1);
            i++;

        } else {
            sorted = true;
        }
    }

    @Override
    public boolean isSorted() {
        return sorted || data.size() <= 1;
    }

}