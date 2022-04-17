package cz.harag.vss.cv1

/**
 * The main class.
 *
 * @version 2019-10-15
 * @author Patrik Harag
 */
class Main {

    static void main(String[] args) {
        if (args.length == 0) {
            String[][] params = [
                    ["100000", "5", "7", "9"],
                    ["100000", "10", "18", "20"],
                    ["100000", "-1000", "-200", "100"]
            ]
            params.each {
                main(it)
                println()
            }
        } else {
            process(args[0].toInteger(), args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
        }
    }

    private static void process(int count, double a, double b, double c) {
        def distribution = new Distribution(a, b, c, new Random())

        def histogram = new Histogram(a, c, 20)
        def stats = new StatsStrategy.Efficient()  // Precise / Efficient
        for (int i = 0; i < count; i++) {
            def nextDouble = distribution.nextDouble()
            histogram.add(nextDouble)
            stats.add(nextDouble)
        }

        printf("E_teorie=%f%n", distribution.mean())
        printf("D_teorie=%f%n", distribution.variance())
        printf("E_vypocet=%f%n", stats.countMean())
        printf("D_vypocet=%f%n", stats.countVariance())
        println()
        histogram.print()
    }

}
