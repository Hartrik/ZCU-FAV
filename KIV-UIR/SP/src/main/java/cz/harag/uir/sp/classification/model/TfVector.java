package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.Utils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Frekvenční reprezentace dokumentu. Normováno euklidovskou normou.
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public class TfVector implements FeatureVector<TfVector> {

    private final String category;
    private final Map<String, Double> words;

    /**
     * Vytvoří frekvenční reprezentaci dokumentu.
     *
     * @param data dokument
     */
    public TfVector(Data data) {
        this.category = data.getCategory();
        this.words = normalized(Utils.frequencies(data.getWords().stream()));
    }

    private Map<String, Double> normalized(Map<String, Integer> frequencies) {
        // euklidovská norma
        double sum = frequencies.values().stream().mapToDouble(d -> d * d).sum();
        double norm = Math.sqrt(sum);

        return frequencies.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, e -> (double) e.getValue() / norm
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
    public double distance(TfVector other) {
        return FeatureVector.euclideanDistance(this.words, other.words);
    }

}
