package cz.harag.uir.sp.classification;

import cz.harag.uir.sp.classification.model.FeatureVector;

import java.io.Serializable;

/**
 * Rozhraní pro klasifikátor.
 *
 * @author Patrik Harag
 * @version 2017-04-29
 */
public interface Classifier<T extends FeatureVector<T>> extends Serializable {

    /**
     * Vloží do klasifikátoru trénovací data.
     *
     * @param data vstupní data (jeden dokument)
     */
    void learn(Data data);

    /**
     * Ukončí fázi učení.
     */
    void finalizeLearning();

    /**
     * Klasifikuje dokument.
     *
     * @param data dokument
     * @return třída nebo null
     */
    String classify(Data data);

}
