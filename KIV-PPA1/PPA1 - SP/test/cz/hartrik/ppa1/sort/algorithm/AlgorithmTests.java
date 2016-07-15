package cz.hartrik.ppa1.sort.algorithm;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Obsahuje základní testy pro všechny řadící algoritmy.
 *
 * @version 2015-10-28
 * @author Patrik Harag
 */
@Ignore
public abstract class AlgorithmTests {

    protected int maxArraySize = 10;

    protected abstract Algorithm createInstance(Data<? extends Comparable<?>> data);

    protected void testSort(int size) {
        Random random = new Random();
        List<Integer> ints = IntStream.generate(random::nextInt)
                .limit(size).boxed().collect(Collectors.toList());

        Data<Integer> data = new Data<>(ints);
        Algorithm algorithm = createInstance(data);

        while (!algorithm.isSorted())
            algorithm.nextStep();

        Collections.sort(ints);
        assertArrayEquals(ints.toArray(), data.list().toArray());
    }

    @Test
    public void testEmpty() {
        testSort(0);
    }

    @Test
    public void testSingle() {
        testSort(1);
    }

    @Test
    public void testPair() {
        testSort(2);
        testSort(2);
        testSort(2);
    }

    @Test
    public void testMulti() {
        for (int i = 3; i <= maxArraySize; i++) {
            testSort(i);
        }
    }

}