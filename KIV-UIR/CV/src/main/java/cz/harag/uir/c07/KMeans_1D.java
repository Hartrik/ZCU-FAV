package cz.harag.uir.c07;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Patrik Harag
 * @version 2017-04-06
 */
public class KMeans_1D {

    // 1D

    public List<List<Double>> groups(double[] points, double[] centers) {
        Map<Integer, List<Double>> last, current = null;

        do {
            last = current;
            current = createGroups(points, centers);
            centers = current.values().stream().mapToDouble(this::center).toArray();
        } while (!current.equals(last));

        return new ArrayList<>(current.values());
    }

    private Map<Integer, List<Double>> createGroups(double[] points, double[] centers) {
        return Arrays.stream(points).boxed()
                .collect(Collectors.groupingBy(d -> nearest(d, centers)));
    }

    private int nearest(double a, double[] centers) {
        return IntStream.range(0, centers.length).boxed()
                .min(Comparator.comparingDouble(i -> distance(a, centers[i])))
                .orElseThrow(NoSuchElementException::new);

        // returns index...
    }

    private double distance(double a, double b) {
        return Math.abs(a - b);
    }

    private double center(Collection<Double> points) {
        return points.stream().mapToDouble(d -> d).summaryStatistics().getAverage();
    }

}
