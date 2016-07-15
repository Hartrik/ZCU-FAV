package cz.hartrik.ppa1.sort.algorithm;

/**
 * Abstraktní třída pro snazší implementaci.
 *
 * @version 2015-10-18
 * @author Patrik Harag
 */
abstract class AlgorithmBase<T extends Comparable<T>> implements Algorithm {

    protected final Data<T> data;

    AlgorithmBase(Data<T> data) {
        this.data = data;
    }

}