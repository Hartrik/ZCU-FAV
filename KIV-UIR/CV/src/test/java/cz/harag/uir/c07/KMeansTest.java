package cz.harag.uir.c07;

import cz.hartrik.common.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Patrik Harag
 * @version 2017-04-09
 */
public class KMeansTest {

    private <T> void print(List<List<T>> groups) {
        System.out.println("Groups:");
        groups.forEach(System.out::println);
    }

    // --- k-means

    @Test
    public void k_means_1D() {
        KMeans_1D kMeans = new KMeans_1D();
        List<List<Double>> groups = kMeans.groups(
                new double[]{2, 4, 15, 18, 5, 50, 30, 34, 65},
                new double[]{2, 4}
        );

        print(groups);
    }

    @Test
    public void k_means_gen_1D() {
        KMeans_Generic<Double> kMeans = new KMeans_Generic<>(
                Points::distance1D, Points::center1D);

        List<List<Double>> groups = kMeans.groups(
                Arrays.asList(2., 4., 15., 18., 5., 50., 30., 34., 65.),
                Arrays.asList(2., 4.)
        );

        print(groups);
    }

    @Test
    public void k_means_gen_2D() {
        KMeans_Generic<Pair<Double, Double>> kMeans = new KMeans_Generic<>(
                Points::distance2D, Points::center2D);

        List<List<Pair<Double, Double>>> groups = kMeans.groups(
                Arrays.asList(
                        Pair.of(1.0, 1.0),
                        Pair.of(1.5, 2.0),
                        Pair.of(3.0, 4.0),
                        Pair.of(5.0, 7.0),
                        Pair.of(3.5, 5.0),
                        Pair.of(4.5, 5.0),
                        Pair.of(3.5, 4.5)
                ),
                Arrays.asList(
                        Pair.of(1.0, 1.0),
                        Pair.of(5.0, 7.0)
                )
        );

        // Cluster 1	1, 2
        // Cluster 2	3, 4, 5, 6, 7

        print(groups);
    }

    // --- k-medoids

    @Test
    public void k_medoids_gen_2D() {
        KMeans_Generic<Pair<Double, Double>> kMeans = new KMeans_Generic<>(
                Points::distance2D, Points::centerMedoids2D);

        List<List<Pair<Double, Double>>> groups = kMeans.groups(
                Arrays.asList(
                        Pair.of(1.0, 1.0),
                        Pair.of(1.5, 2.0),
                        Pair.of(3.0, 4.0),
                        Pair.of(5.0, 7.0),
                        Pair.of(3.5, 5.0),
                        Pair.of(4.5, 5.0),
                        Pair.of(3.5, 4.5)
                ),
                Arrays.asList(
                        Pair.of(1.0, 1.0),
                        Pair.of(5.0, 7.0)
                )
        );

        // Cluster 1	1, 2
        // Cluster 2	3, 4, 5, 6, 7

        print(groups);
    }

}
