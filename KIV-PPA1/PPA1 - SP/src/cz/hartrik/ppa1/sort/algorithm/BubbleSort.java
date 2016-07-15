package cz.hartrik.ppa1.sort.algorithm;

/**
 * Implementace řadícího algoritmu Bubble Sort.
 *
 * @version 2015-10-21
 * @author Patrik Harag
 * @param <T> typ řazených prvků
 */
public class BubbleSort<T extends Comparable<T>> extends AlgorithmBase<T> {

    private boolean sorted;
    private int i;  // index vnějšího cyklu => počet seřazených prvků
    private int j;  // index vnitřního cyklu

    /**
     * Vytvoří novou instanci.
     *
     * @param data data, která se budou řadit
     */
    public BubbleSort(Data<T> data) {
        super(data);
    }

    @Override
    public void nextStep() {
        if (isSorted()) return;

        if (i < data.size() - 1) {
            if (j < data.size() - 1 - i) {
                if (data.compare(j, j + 1) > 0) {
                    data.swap(j, j + 1);
                }
                j++;

            } else {
                // konec vnitřního cyklu
                j = 0; i++;
                nextStep();
            }
        } else {
            // konec vnějšího cyklu
            sorted = true;
        }
    }

    @Override
    public boolean isSorted() {
        return sorted || data.size() <= 1;
    }

}