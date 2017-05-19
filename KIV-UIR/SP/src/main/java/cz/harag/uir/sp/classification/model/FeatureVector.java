package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.Config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Rozhraní pro vektor příznaků.
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public interface FeatureVector<T extends FeatureVector<T>> extends Serializable {

    /**
     * Vrátí mapu, kde klíč je slovo a hodnota jeho váha.
     *
     * @return mapa
     */
    Map<String, Double> getWords();

    /**
     * Vypočte vzdálenost vůči dalšímu vektoru.
     *
     * @param vec vektor
     * @return vzdálenost
     */
    double distance(T vec);

    /**
     * Vrátí třídu.
     *
     * @return třída nebo null
     */
    String getCategory();

    /**
     * Určí euklidovskou vzdálenost dvou vektorů, které jsou reprezentovány
     * mapami.
     *
     * @param u první 'vektor'
     * @param v druhý 'vektor'
     * @return vzdálenost
     */
    static double euclideanDistance(Map<String, Double> u, Map<String, Double> v) {
        Set<String> words = new HashSet<>();
        words.addAll(u.keySet());
        words.addAll(v.keySet());

        double r = words.stream().mapToDouble(word -> {
            // (a1 - b1), (a2 - b2)...

            double a = u.getOrDefault(word, 0.);
            double b = v.getOrDefault(word, 0.);

            if (Config.DEBUG) {
                String collision = (a > 0 && b > 0) ? "collision" : "";
                System.out.printf("%-30s   %.6f  %.6f  %s%n", word, a, b, collision);
            }

            return a - b;

        }).map(i -> i * i).sum();

        return Math.sqrt(r);
    }

}
