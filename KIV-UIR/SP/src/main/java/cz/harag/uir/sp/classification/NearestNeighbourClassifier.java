package cz.harag.uir.sp.classification;

import cz.harag.uir.sp.Config;
import cz.harag.uir.sp.classification.model.FeatureVector;
import cz.harag.uir.sp.classification.model.Model;
import cz.hartrik.common.Pair;

import java.util.Comparator;
import java.util.List;

/**
 * Implementace metody nejbližšího souseda.
 *
 * @author Patrik Harag
 * @version 2017-05-09
 */
public class NearestNeighbourClassifier<T extends FeatureVector<T>>
        implements Classifier<T> {

    private final Model<T> model;
    private boolean postProcessed = false;

    /**
     * Vytvoří klasifikátor podle minimální vzdálenosti.
     *
     * @param model model
     */
    public NearestNeighbourClassifier(Model<T> model) {
        this.model = model;
    }

    private void checkPostProcess() {
        if (!postProcessed) {
            List<Data> inputData = model.getInputData();

            if (inputData.isEmpty())
                throw new IllegalStateException("Training set is empty!");

            if (Config.DEBUG) {
                System.out.println("Postprocessing:");
                System.out.println("Before: " + Utils.words(model.getInputData()).count());
            }

            postProcess(model);
            postProcessed = true;

            if (Config.DEBUG) {
                System.out.println("After: " + Utils.words(model.getInputData()).count());
                System.out.println();
            }
        }
    }

    private void postProcess(Model<T> model) {
        model.commit(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void learn(Data data) {
        if (postProcessed)
            throw new IllegalStateException();

        model.add(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finalizeLearning() {
        checkPostProcess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String classify(Data data) {
        checkPostProcess();

        T unknown = model.createUnknownVector(data);

        return model.getFeatureVectors().parallelStream()
                .map(vec -> Pair.of(vec, distance(vec, unknown)))
                .min(Comparator.comparingDouble(Pair::getSecond))
                    .map(Pair::getFirst)
                    .map(FeatureVector::getCategory)
                    .orElseGet(null);
    }

    /**
     * Určí vzdálenost dvou vektorů.
     *
     * @param vec vektor A
     * @param unknown vektor B
     * @return vzdálenost
     */
    private double distance(T vec, T unknown) {
        return vec.distance(unknown);
    }

}
