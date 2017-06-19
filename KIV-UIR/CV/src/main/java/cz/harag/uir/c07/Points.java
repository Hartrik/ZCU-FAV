package cz.harag.uir.c07;

import cz.hartrik.common.Pair;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * @author Patrik Harag
 * @version 2017-04-09
 */
public class Points {

    // --- k-means

    // 1D

    public static Double distance1D(Double a, Double b) {
        return Math.abs(a - b);
    }

    public static Double center1D(Collection<Double> points) {
        return points.stream().mapToDouble(d -> d).summaryStatistics().getAverage();
    }

    // 2D

    public static Double distance2D(Pair<Double, Double> a, Pair<Double, Double> b) {
        return Math.sqrt(
                Math.pow(b.getFirst() - a.getFirst(), 2) +
                Math.pow(b.getSecond() - a.getSecond(), 2));
    }

    public static Pair<Double, Double> center2D(Collection<Pair<Double, Double>> points) {
        return Pair.of(
                points.stream().mapToDouble(Pair::getFirst).sum() / points.size(),
                points.stream().mapToDouble(Pair::getSecond).sum() / points.size()
        );
    }

    // -- k-medoids

    public static Pair<Double, Double> centerMedoids2D(Collection<Pair<Double, Double>> points) {
        Pair<Double, Double> center = center2D(points);

        // search for nearest point
        return points.stream()
                .min(Comparator.comparingDouble(p -> distance2D(center, p)))
                .orElseThrow(NoSuchElementException::new);
    }

}
