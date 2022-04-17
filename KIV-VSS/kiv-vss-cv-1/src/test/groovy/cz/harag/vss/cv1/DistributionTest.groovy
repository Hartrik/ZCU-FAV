package cz.harag.vss.cv1

import org.junit.Test

/**
 *
 * @author Patrik Harag
 */
class DistributionTest {

    @Test
    void testBounds() {
        double a = 15.2
        double b = 16
        double c = 21

        def distribution = new Distribution(a, b, c, new Random(42))
        for (int i = 0; i < 1000; i++) {
            def d = distribution.nextDouble()
            assert d > a
            assert d < c
        }
    }

}
