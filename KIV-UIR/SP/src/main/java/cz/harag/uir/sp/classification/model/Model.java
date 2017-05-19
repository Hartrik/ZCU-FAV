package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.processing.Postprocessor;
import cz.harag.uir.sp.classification.processing.Preprocessor;

import java.io.Serializable;
import java.util.List;

/**
 * Rozhraní pro model.
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public interface Model<T extends FeatureVector<T>> extends Serializable {

    /**
     * Nastaví pre-processor.
     *
     * @param p post-processor
     */
    void setPreprocessor(Preprocessor p);

    /**
     * Nastaví post-processor.
     *
     * @param p post-processor
     */
    void setPostprocessor(Postprocessor p);

    /**
     * Přidá do modelu další dokument (trénovací data).
     *
     * @param data dokument
     */
    void add(Data data);

    /**
     * Vrátí seznam vstupních dokumentů.
     *
     * @return dokumenty
     */
    List<Data> getInputData();

    /**
     * Ukončí fázi učení klasifikátoru - provede se post-processing.
     * Poté budou všechny feature vectory připraveny.
     */
    void commit(boolean noVectors);

    /**
     * Vrátí všechny dostupné feature vectory.
     *
     * @return vektory
     */
    List<T> getFeatureVectors();

    /**
     * Vytvoří feature vector z dokumentu u kterého neznáme třídu (testovací data).
     *
     * @param data dokument
     * @return vektor
     */
    T createUnknownVector(Data data);

}
