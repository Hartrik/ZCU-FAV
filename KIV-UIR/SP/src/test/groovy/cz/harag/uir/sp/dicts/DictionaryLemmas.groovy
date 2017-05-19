package cz.harag.uir.sp.dicts

import org.junit.Ignore
import org.junit.Test

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 *
 * @version 2017-04-06
 * @author Patrik Harag
 */
class DictionaryLemmas {

    @Test
    @Ignore
    void createDictionary() {
        def file = Paths.get("src/test/resources/lemmatization-cs.txt")
        def out = Paths.get("target/lemma-cs.txt")

        def lines = Files.lines(file, StandardCharsets.UTF_8)
                .map { it.trim() }
                .filter { !it.isEmpty() }
                .map { it.toLowerCase() }
                .collect(Collectors.toList())

        lines.add(0, lines.size().toString())

        Files.write(out, lines, StandardCharsets.UTF_8)
    }

}
