package cz.hartrik.ppa1.sort.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Spravuje všechny podporované řadící algoritmy.
 *
 * @version 2015-10-25
 * @author Patrik Harag
 */
public final class Algorithms {

    private Algorithms() {}

    private static final Map<String, Function<Data<?>, Algorithm>> factories;

    static {
        factories = new LinkedHashMap<>();
        factories.put("Bubble Sort", BubbleSort::new);
        factories.put("Insertion Sort", InsertionSort::new);
        factories.put("Random Sort", BogoSort::new);
        factories.put("Selection Sort", SelectionSort::new);
    }

    /**
     * Vytvoří algoritmus podle jména.
     *
     * @param name jméno algoritmu
     * @param data data, která se budou třídit
     * @return řadící algoritmus
     * @throws NullPointerException algoritmus nebyl nalezen
     */
    public static Algorithm createByName(String name, Data<?> data) {
        return factories.get(name).apply(data);
    }

    /**
     * Vrátí seznam s názvy všech podporovaných algoritmů.
     *
     * @return seznam
     */
    public static Set<String> getSupportedAlgorithms() {
        return factories.keySet();
    }

    /**
     * Vrátí {@code true}, pokud je algoritmus s daným názvem podporován.
     *
     * @param algName název algoritmu
     * @return boolean
     */
    public static boolean isSupported(String algName) {
        return factories.containsKey(algName);
    }

}