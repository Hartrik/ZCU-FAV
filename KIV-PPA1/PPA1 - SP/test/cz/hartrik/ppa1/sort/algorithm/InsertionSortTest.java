package cz.hartrik.ppa1.sort.algorithm;

/**
 *
 * @version 2015-10-28
 * @author Patrik Harag
 */
public class InsertionSortTest extends AlgorithmTests {

    @Override
    public Algorithm createInstance(Data<? extends Comparable<?>> data) {
        return new InsertionSort<>(data);
    }

}
