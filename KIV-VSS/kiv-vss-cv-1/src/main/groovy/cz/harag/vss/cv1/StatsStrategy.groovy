package cz.harag.vss.cv1

/**
 * Tool for mean and variance counting.
 *
 * @version 2019-10-15
 * @author Patrik Harag
 */
interface StatsStrategy {

    void add(double val)

    double countMean()

    double countVariance()

    static class Precise implements StatsStrategy {
        private int count = 0
        private BigDecimal sum = 0
        private List<Double> numbers = []

        @Override
        void add(double nextDouble) {
            count++

            sum += nextDouble
            numbers.add(nextDouble)
        }

        @Override
        double countMean() {
            return sum / count
        }

        @Override
        double countVariance() {
            double mean = countMean()
            return numbers.collect { (it - mean) ** 2}.sum() / count
        }
    }

    static class Efficient implements StatsStrategy {
        private int count = 0
        private BigDecimal sum = 0
        private BigDecimal varianceSum = 0

        @Override
        void add(double nextDouble) {
            count++
            sum += nextDouble
            varianceSum += (nextDouble - countMean()) ** 2
        }

        @Override
        double countMean() {
            return sum / count
        }

        @Override
        double countVariance() {
            return varianceSum / count
        }
    }

}