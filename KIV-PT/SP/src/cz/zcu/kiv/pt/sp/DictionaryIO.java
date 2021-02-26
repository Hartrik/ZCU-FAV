package cz.zcu.kiv.pt.sp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Poskytuje nástroje k načítání a ukládání slovníku.
 *
 * @version 2016-09-28
 * @author Patrik Harag
 */
public class DictionaryIO {

    private DictionaryIO() {}

    /**
     * Načte slovník z textového souboru s kódováním UTF-8.
     * Na každé řádce je jedno slovo.
     *
     * @param file cesta k souboru
     * @return slovník
     * @throws IOException
     */
    public static Dictionary loadDictionary(Path file) throws IOException {
        Dictionary dict = new HashSetDictionary();

        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            lines.filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(dict::add);
        }

        return dict;
    }

    /**
     * Uloží slovník do textového souboru s kódováním UTF-8.
     *
     * @param file cesta k souboru
     * @param dict slovník
     * @throws IOException
     */
    public static void writeDictionary(Path file, Dictionary dict) throws IOException {
        Files.write(file, dict.asSet(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

}