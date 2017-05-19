package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementace IdfProvider.
 *
 * @author Patrik Harag
 * @version 2017-04-17
 */
public class IdfProviderImpl implements IdfProvider {

    private final Map<String, Double> idfs;

    public IdfProviderImpl(Map<String, Double> idfs) {
        this.idfs = idfs;
    }

    @Override
    public double countIdf(String word) {
        return idfs.getOrDefault(word, 1.);
    }

    static IdfProvider of(Collection<Data> dataSet) {
        Map<String, Integer> dfs = new HashMap<>();

        // určení document frequency
        dataSet.forEach(data -> {
            Utils.uniqueWords(data).forEach(word -> {
                int count = dfs.containsKey(word) ? dfs.get(word) : 0;
                dfs.put(word, count + 1);
            });
        });

        // určení inverse document frequency
        Map<String, Double> idfs = new HashMap<>(dfs.size());
        double documents = dataSet.size();  // úmyslně double

        dfs.entrySet().forEach(entry -> {
            double idf = Math.log(documents / (1 + entry.getValue()));
            idfs.put(entry.getKey(), idf);
        });

        return new IdfProviderImpl(idfs);
    }

}
