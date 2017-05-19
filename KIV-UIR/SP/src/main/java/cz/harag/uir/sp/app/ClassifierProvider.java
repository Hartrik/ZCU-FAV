package cz.harag.uir.sp.app;

import cz.harag.uir.sp.classification.NaiveBayesClassifier;
import cz.harag.uir.sp.classification.Classifier;
import cz.harag.uir.sp.classification.NearestNeighbourClassifier;
import cz.harag.uir.sp.classification.model.Model;

import java.util.function.Function;

/**
 * Výčtový typ dostupných klasifikátorů.
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public enum ClassifierProvider {

    NEAREST_NEIGHBOUR("1nn", NearestNeighbourClassifier::new),
    NAIVE_BAYES("naive-bayes", NaiveBayesClassifier::new);

    private final String name;
    private final Function<Model<?>, Classifier<?>> provider;

    ClassifierProvider(String name, Function<Model<?>, Classifier<?>> provider) {
        this.name = name;
        this.provider = provider;
    }

    /**
     * Vrátí jméno klasifikátoru.
     *
     * @return jméno
     */
    public String getName() {
        return name;
    }

    /**
     * Vytvoří klasifikátor.
     *
     * @param model model
     * @return klasifikátor
     */
    public Classifier<?> createClassifier(Model<?> model) {
        return provider.apply(model);
    }
}
