package cz.harag.uir.c06;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * @author Patrik Harag
 * @version 2017-03-29
 */
public class KnapsackProblemTest {

    private final Random random = new Random(515);
    private final KnapsackProblem problem = new KnapsackProblem(random);

    @Test
    public void test() {
        List<KnapsackItem> result = problem.fill(40, Arrays.asList(
                new KnapsackItem(10, 5),
                new KnapsackItem(8, 11),
                new KnapsackItem(4, 7),
                new KnapsackItem(18, 14),
                new KnapsackItem(5, 3),
                new KnapsackItem(17, 10)
        ));

        int mass = result.stream().mapToInt(KnapsackItem::getMass).sum();
        int price = result.stream().mapToInt(KnapsackItem::getPrice).sum();

        System.out.println("price = " + price);
        System.out.println("mass = " + mass);
        System.out.println("content = " + result);

        assertTrue(mass <= 40);
    }

}
