package cz.zcu.kiv.pro.tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @version 2016-11-24
 * @author Patrik Harag
 */
@RunWith(Parameterized.class)
public class PerformanceTest {

    public static int TESTS_COUNT = 1000;

    @Parameters()
    public static Collection<Object[]> data() {
        List<Object[]> list = Arrays.asList(new Object[][] {
            { generateTree(6, 15) },
            { generateTree(8, 10) },
            { generateTree(8, 25) },
            { generateTree(9, 30) },
        });

        int i = 0;
        for (Object[] array : list) {
            Tree tree = (Tree) array[0];
            System.out.println(i++ + " / " + tree.getNodes().size());
        }

        return list;
    }

    public static Tree generateTree(int maxWidth, int maxDepth) {
        return new TreeGenerator(maxWidth, maxDepth).generateTree();
    }

    private final Tree tree;

    public PerformanceTest(Tree tree) {
        this.tree = tree;
    }

    @Test
    public void testSimpleLayout() {
        for (int i = 0; i < TESTS_COUNT; i++) {
            Layout layout = new SimpleLayout(10, 10);
            layout.make(tree);
        }
    }

    @Test
    public void testAdvancedLayout() {
        for (int i = 0; i < TESTS_COUNT; i++) {
            Layout layout = new AdvancedLayout(10, 10);
            layout.make(tree);
        }
    }

    @Test
    public void testOptimizedAdvancedLayout() {
        for (int i = 0; i < TESTS_COUNT; i++) {
            Layout layout = new AdvancedLayoutOpt(10, 10);
            layout.make(tree);
        }
    }

}