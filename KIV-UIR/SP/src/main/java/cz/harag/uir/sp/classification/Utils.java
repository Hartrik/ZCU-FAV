package cz.harag.uir.sp.classification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Obsahuje pomocné metody-
 *
 * @author Patrik Harag
 * @version 2017-04-20
 */
public class Utils {

    public static Stream<String> words(Collection<Data> data) {
        return data.stream().flatMap(d -> d.getWords().stream());
    }

    public static Map<String, List<Data>> groupByCategory(Collection<Data> data) {
        return data.stream().collect(Collectors.groupingBy(Data::getCategory));
    }

    public static Set<String> uniqueWords(Collection<Data> data) {
        return words(data).collect(Collectors.toSet());
    }

    public static Set<String> uniqueWords(Data data) {
        return data.getWords().stream().collect(Collectors.toSet());
    }

    public static Map<String, Integer> frequencies(Stream<String> words) {
        return words.collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.reducing(0, e -> 1, Integer::sum)));
    }

    public static List<Data> reduce(List<Data> dataSet) {
        return Utils.groupByCategory(dataSet).entrySet().stream().map(entry -> {
            String category = entry.getKey();
            List<Data> data = entry.getValue();

            List<String> words = Utils.words(data).collect(Collectors.toList());
            return new Data(category, words);

        }).collect(Collectors.toList());
    }

}
