package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.Utils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Model používající TF-IDF reprezentaci.
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public class TfIdfVector implements FeatureVector<TfIdfVector> {

    private final String category;

    private final IdfProvider idfProvider;
    private final Map<String, Double> words;
    private final Map<String, Double> idfs;

    public TfIdfVector(Data data, IdfProvider idfProvider) {
        this.category = data.getCategory();
        this.idfProvider = idfProvider;
        this.words = normalized(Utils.frequencies(data.getWords().stream()));

        this.idfs = words.keySet().stream().collect(Collectors.toMap(
                w -> w,
                w -> idfProvider.countIdf(w)
        ));
    }

    private Map<String, Double> normalized(Map<String, Integer> frequencies) {
        int count = frequencies.values().stream().mapToInt(d -> d).sum();

        return frequencies.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> (double) e.getValue() / count * idfProvider.countIdf(e.getKey())  // tf * idf
        ));
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
    public Map<String, Double> getWords() {
        return idfs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double distance(TfIdfVector other) {
        return FeatureVector.euclideanDistance(this.words, other.words);
    }

}
