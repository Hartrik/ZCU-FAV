package cz.harag.uir.c07;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Patrik Harag
 * @version 2017-04-06
 */
public class KMeans_Generic<P> {

    private final BiFunction<P, P, Double> distance;
    private final Function<Collection<P>, P> center;

    public KMeans_Generic(BiFunction<P, P, Double> distance, Function<Collection<P>, P> center) {
        this.distance = distance;
        this.center = center;
    }

    public List<List<P>> groups(List<P> points, List<P> centers) {
        Collection<List<P>> last, current = null;

        do {
            last = current;
            current = createGroups(points, centers);
            centers = createCenters(current);
        } while (!current.equals(last));

        return new ArrayList<>(current);
    }

    private Collection<List<P>> createGroups(List<P> points, List<P> centers) {
        Collection<List<P>> groups = points.stream()
                .collect(Collectors.groupingBy(d -> nearest(d, centers)))
                .values();

        // ordered collection for comparing
        return new ArrayList<>(groups);
    }

    private List<P> createCenters(Collection<List<P>> groups) {
        return groups.stream().map(center).collect(Collectors.toList());
    }

    private int nearest(P a, List<P> centers) {
        // returns index...
        return IntStream.range(0, centers.size()).boxed()
                .min(Comparator.comparingDouble(i -> distance.apply(a, centers.get(i))))
                .orElseThrow(NoSuchElementException::new);
    }
}