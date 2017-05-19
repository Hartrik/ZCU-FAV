package cz.harag.uir.sp.classification

import org.junit.Ignore
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.Function
import java.util.stream.Collectors

/**
 *
 * @version 2017-04-05
 * @author Patrik Harag
 */
class DataLoaderTest {

    private static String LEARN_DATA = "data/learn/"

    @Ignore
    @Test
    void printCategories() {
        println Files.list(Paths.get(LEARN_DATA))
                .map { DataLoader.loadFile(it).category }
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .forEach { k, v -> println "[$v] $k" }
    }

    @Ignore
    @Test
    void printWords() {
        Files.list(Paths.get(LEARN_DATA))
                .flatMap() { DataLoader.loadFile(it).words.stream() }
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingLong { it.value }.reversed())
                .forEach { println "[$it.value] $it.key" }
    }

}
