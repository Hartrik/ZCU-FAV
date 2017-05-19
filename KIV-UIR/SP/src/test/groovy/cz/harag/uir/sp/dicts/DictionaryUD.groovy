package cz.harag.uir.sp.dicts

import cz.harag.uir.sp.classification.Utils
import org.junit.Ignore
import org.junit.Test

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 *
 * @version 2017-04-06
 * @author Patrik Harag
 */
class DictionaryUD {


    @Test
    @Ignore
    void createDictionary() {

        // odebráno kvůli velikosti...
        def files = [
                "src/test/resources/cs-ud-dev.conllu",
                "src/test/resources/cs-ud-train-c.conllu",
                "src/test/resources/cs-ud-train-l.conllu",
                "src/test/resources/cs-ud-train-m.conllu",
                "src/test/resources/cs-ud-train-v.conllu",
        ]

        def lines = files.stream().flatMap { process(Paths.get(it)) }
                    .distinct()  // odstranění duplicit
                    .collect(Collectors.toList())

        //writePos(lines)
        //writeLemm(lines)
        //writeStopW(lines)
    }

    private void writeStopW(lines) {
        def out = Paths.get("target/stop.txt")

        def lemmLines = lines.stream()
                .filter { it[2] in ['ADP', 'AUX', 'CONJ', 'DET', 'INTJ', 'PART', 'PRON', 'SCONJ'] }
                    // http://universaldependencies.org/cs/pos/index.html
                .map { it[0] }
                .filter { it.size() > 1 }
                .distinct()
                .sorted()
                .collect(Collectors.toList())

        lemmLines.add(0, lemmLines.size().toString())
        Files.write(out, lemmLines, StandardCharsets.UTF_8)
    }

    private void writeLemm(lines) {
        def out = Paths.get("target/lemma.txt")

        def lemmLines = lines.stream()
                .map { [ it[0], it[1]] }
                .filter {  it[0] != it[1] }
                .filter {  it[0].size() > 1 }
                .distinct()
                .map { "" + it[0] + "\t" + it[1] }
                .sorted()
                .collect(Collectors.toList())

        lemmLines.add(0, lemmLines.size().toString())
        Files.write(out, lemmLines, StandardCharsets.UTF_8)
    }

    private void writePos(lines) {
        def posOut = Paths.get("target/pos.txt")

        def posLines = lines.stream()
                .map { [ it[0], it[2]] }
                .collect(Collectors.toList())

        def frequencies = Utils.frequencies(lines.stream().map { it[0] }).entrySet().stream()
                .filter { it.value > 1 }
                .peek { println it }
                .forEach { i -> posLines.removeIf { j -> j[0] == i } }
                // r same with different pos


        posLines = posLines.stream()
                .map { "" + it[0] + "\t" + it[1] }
                .sorted()
                .collect(Collectors.toList())


        posLines.add(0, posLines.size().toString())
        Files.write(posOut, posLines, StandardCharsets.UTF_8)
    }

    private Stream<List<String>> process(Path file) {
        return Files.lines(file, StandardCharsets.UTF_8)
                .map { it.trim() }
                .filter { !it.isEmpty() }
                .filter { !it.startsWith("#") }
                .map { processLine(it) }
                .filter { !(it[2] in ['NUM', 'PUNCT', 'SYM', 'X']) }  // http://universaldependencies.org/cs/pos/index.html
                .filter { !((it[1] == '_') && (it[2] == '_')) }
    }

    private List<String> processLine(String line) {
        // example:

        // 2	Rady	rada	NOUN
        // 5	|	|	PUNCT	Z:-------------	_	2	punct	_	_
        //
        // # newpar id = cmpr9406-009-p5
        // #

        def split = line.split("\t")

        if (split.size() < 4) {
            throw new RuntimeException()
        }

        String w = split[1]
        String l = split[2]
        String pos = split[3]

        [w.toLowerCase(), l.toLowerCase(), pos]
    }

}
