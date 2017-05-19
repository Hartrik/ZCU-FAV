package cz.harag.uir.sp.classification;

import cz.harag.uir.sp.Config;
import cz.harag.uir.sp.classification.model.FeatureVector;
import cz.harag.uir.sp.classification.model.Model;
import cz.hartrik.common.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Naivní Bayesův klasifikátor.
 *
 * @author Patrik Harag
 * @version 2017-05-09
 */
public class NaiveBayesClassifier<T extends FeatureVector<T>>
        implements Classifier<T> {

    private final Model<T> model;
    private boolean postProcessed = false;

    // šance dokumentu na to být ve třídě = počet ve třídě / počet celkem; <třída, hodnota>
    private Map<String, Double> aprioriProbabilities;

    // podmíněné pravděpodobnosti; <třída<slovo, hodnota>>
    private Map<String, Map<String, Double>> condProbabilities;

    // maximální podmíněné pravděpodobnosti; <slovo, hodnota>
    private Map<String, Double> maxCond;

    // minimální podmíněná pravděpodobnost
    private double minCond;

    /**
     * Vytvoří naivní Bayesův klasifikátor.
     *
     * @param model model
     */
    public NaiveBayesClassifier(Model<T> model) {
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
        model.commit(true);

        List<Data> inputData = model.getInputData();
        Map<String, List<Data>> categories = Utils.groupByCategory(inputData);

        // apriorní
        // šance dokumentu na to být ve třídě = počet ve třídě / počet celkem

        aprioriProbabilities = categories.entrySet().stream().collect(Collectors.toMap(
                e -> e.getKey(),
                e -> (double) e.getValue().size() / inputData.size()
        ));

        // podmíněná

        condProbabilities = categories.entrySet().parallelStream().collect(Collectors.toMap(
                e -> e.getKey(),
                e -> {
                    List<Data> dataSet = e.getValue();
                    Set<String> words = Utils.uniqueWords(dataSet);

                    return words.stream().collect(Collectors.toMap(
                            w -> w,
                            w -> {
                                // vzorky z třídy mající slovo / vzorky z třídy
                                long contains = dataSet.stream()
                                        .filter(d -> d.getWords().contains(w))
                                        .count();

                                return (double) contains / dataSet.size();
                            }
                    ));
                }
        ));

        // maximální podmíněná pro každé slovo (kvůli normalizaci)

        maxCond = new HashMap<>();
        condProbabilities.values().forEach(map -> {
            map.forEach((String w, Double r) -> {
                Double max = maxCond.get(w);
                maxCond.put(w, max == null ? r : Math.max(max, r));
            });
        });

        // minimální podmíněná

        minCond = condProbabilities.values().stream()
                .flatMap(s -> s.entrySet().stream())
                .map(e -> e.getValue() / maxCond.get(e.getKey()))
                .min(Comparator.comparingDouble(d -> d)).orElse(0.);
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

        Comparator<Pair<String, Double>> comparator = Comparator
                .comparingDouble(Pair::getSecond);

        final int HEAP_SIZE = 1;  // uchovává pouze n nejlepších
        PriorityQueue<Pair<String, Double>> heap = new PriorityQueue<>(comparator);

        for (String category : aprioriProbabilities.keySet()) {
            double probability = probability(category, unknown);

            heap.add(Pair.of(category, probability));

            // odstranění vektoru s nejmenší hodnotou, je-li potřeba
            if (heap.size() > HEAP_SIZE)
                heap.remove();
        }

        if (Config.DEBUG) {
            // vypíše pár nejbližších
            System.out.println();
            heap.stream().sorted(comparator.reversed())
                    .forEach(p -> System.out.println(p + " /" + p.getFirst()));
        }

        return heap.stream().sorted(comparator.reversed()).findFirst()
                .map(Pair::getFirst)
                .orElse(null);
    }

    private double probability(String category, T unknown) {

        double apr = aprioriProbabilities.getOrDefault(category, 0.);
        double result = apr;

        for (Map.Entry<String, Double> entry : unknown.getWords().entrySet()) {
            Double cond = condProbabilities.get(category).get(entry.getKey());
            if (cond != null) {
                Double max = maxCond.get(entry.getKey());
                // normujeme největší hodnotou, aby tolik nedocházelo k podtečení
                result *= cond/max;
            } else {
                // vynásobíme to něčím malým, aby došlo k penalizaci
                result *= minCond;
            }
        }

        return result;
    }

}