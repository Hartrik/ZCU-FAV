package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.Utils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Binární reprezentace dokumentu.
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public class BinVector implements FeatureVector<BinVector> {

    private final String category;
    private final Map<String, Double> words;

    /**
     * Vytvoří binární reprezentaci dokumentu.
     *
     * @param data dokument
     */
    public BinVector(Data data) {
        this.category = data.getCategory();
        this.words = Utils.uniqueWords(data).stream().collect(Collectors.toMap(
                w -> w, w -> 1.
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Double> getWords() {
        return words;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCategory() {
        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double distance(BinVector other) {
        return FeatureVector.euclideanDistance(this.words, other.words);
    }

}
