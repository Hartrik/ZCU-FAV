package cz.harag.vss.cv1

/**
 * Creates histogram.
 *
 * @version 2019-10-09
 * @author Patrik Harag
 */
class Histogram {

    private final double a, b
    private final int buckets
    private double delta
    private int[] histogram

    /**
     * Creates a new instance.
     *
     * @param a min value
     * @param b max value
     * @param buckets number of ranges between a and b
     */
    Histogram(double a, double b, int buckets) {
        this.a = a
        this.b = b
        this.buckets = buckets
        this.delta = (b - a) / buckets
        this.histogram = new int[buckets]
    }

    void add(double val) {
        assert val > a && val < b

        double scaled = val - a
        for (int i = 0; i < buckets; i++) {
            if (scaled < (i + 1) * this.delta) {
                histogram[i]++
                break
            }
        }
    }

    void print() {
        int max = histogram.toList().max()
        int maxStars = 80

        for (int i = 0; i < buckets; i++) {
            printf("%f %-80s %d %n",
                    a + (i * delta),
                    "*" * (maxStars / max * histogram[i]),
                    histogram[i])
        }
    }
}
