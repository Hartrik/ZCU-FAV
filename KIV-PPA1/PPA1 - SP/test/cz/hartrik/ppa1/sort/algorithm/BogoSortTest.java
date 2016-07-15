package cz.hartrik.ppa1.sort.algorithm;

/**
 * @version 2015-10-28
 * @author Patrik Harag
 */
public class BogoSortTest extends AlgorithmTests {

    { maxArraySize = 5; }  // je to docela neefektivní algoritmus...

    @Override
    public Algorithm createInstance(Data<? extends Comparable<?>> data) {
        return new BogoSort<>(data);
    }

}