package cz.harag.uir.sp.classification;

import cz.hartrik.common.io.NioUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Třída načítající textové dokumenty.
 * Název ve formátu [id]_[třída-1]_[třída-2]...
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public class DataLoader {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int WORDS_PATTERN_FLAGS = Pattern.UNICODE_CHARACTER_CLASS;
    private static final Pattern WORDS_PATTERN = Pattern.compile("\\W+", WORDS_PATTERN_FLAGS);

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("_");


    /**
     * Načte textový dokument.
     *
     * @param file soubor s dokumentem
     * @return dokument
     */
    public static Data loadFile(Path file) throws IOException {
        return new Data(determineCategory(file), loadWords(file));
    }

    private static String determineCategory(Path file) {
        String fileName = NioUtil.removeExtension(file.getFileName().toString());

        return FILE_NAME_PATTERN
                .splitAsStream(fileName)
                .skip(1)  // id
                .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Wrong filename format"));
    }

    private static List<String> loadWords(Path file) throws IOException {
        return Files.lines(file, CHARSET)
                .flatMap(WORDS_PATTERN::splitAsStream)
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());
    }

}
