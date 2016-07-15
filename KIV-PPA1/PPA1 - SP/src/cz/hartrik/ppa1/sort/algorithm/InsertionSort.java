package cz.hartrik.ppa1.sort.algorithm;

/**
 * Implementace řadícího algoritmu Insertion Sort.
 *
 * @version 2015-10-25
 * @author Patrik Harag
 * @param <T> typ tříděných dat
 */
public class InsertionSort<T extends Comparable<T>> extends AlgorithmBase<T> {

    private boolean sorted;
    private int i = 1;  // index vnějšího cyklu => počet seřazených prvků

    /**
     * Vytvoří novou instanci.
     *
     * @param data data, která se budou řadit
     */
    public InsertionSort(Data<T> data) {
        super(data);
    }

    @Override
    public void nextStep() {
        if (isSorted())
            return;

        if (i < data.size()) {
            // nalezení pozice, do které bude prvek s indexem i vložen
            int j = i;
            while (j > 0 && data.compare(j - 1, i) > 0)
                j--;

            // vložení
            if (i != j)
                data.insert(i, j);

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