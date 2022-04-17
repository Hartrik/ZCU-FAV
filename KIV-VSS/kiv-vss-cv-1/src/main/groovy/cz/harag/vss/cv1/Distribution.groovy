package cz.harag.vss.cv1

/**
 * Trapezoidal distribution.
 *
 * @version 2019-10-08
 * @author Patrik Harag
 */
class Distribution {

    private final double a, b, c
    private final Random random

    Distribution(double a, double b, double c, Random random) {
        this.a = a
        this.b = b
        this.c = c
        this.random = random
    }

    double nextDouble() {
        final double range = c - a

        Double result = null
        while (result == null) {
            double x = range * random.nextDouble()

            if (x > (b - a)) {
                // "rectangle" part
                result = x
            } else {
                // "triangle" part
                double y = random.nextDouble()
                // check is under line
                if (y < x / (b - a)) {
                    result = x
                } else {
                    // try another point
                }
            }
        }
        return a + result
    }

    double mean() {
        // https://en.wikipedia.org/wiki/Trapezoidal_distribution
        double d = c + 0.000000001
         (1 / (3 * (d + c - b - a))) * (((d**3 - c**3) / (d - c)) - ((b**3 - a**3) / (b - a)))
    }

    double variance() {
        // TODO
        return Double.NaN
    }

}
