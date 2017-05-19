package cz.harag.uir.sp.classification.processing;

import cz.hartrik.common.Exceptions;
import cz.hartrik.common.io.Resources;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Převádí slova do základního tvaru. Používá slovník.
 *
 * @author Patrik Harag
 * @version 2017-05-09
 */
public class Lemmatizer implements Preprocessor.Mapper {

    private static final char SEPARATOR = '\t';

    private final URL resource;
    private final Charset charset;
    private Map<String, String> dictionary;

    /**
     * Vytvoří nový lemmatizer.
     *
     * @param resource url slovníku
     * @param charset znaková sada
     */
    public Lemmatizer(URL resource, Charset charset) {
        this.resource = resource;
        this.charset = charset;
    }

    @Override
    public String apply(String word) {
        Exceptions.unchecked(() -> {
            if (dictionary == null)
                dictionary = loadDictionary();
        });

        String lemma = dictionary.get(word);
        return lemma != null ? lemma : word;
    }

    private Map<String, String> loadDictionary() throws Exception {
        try (Stream<String> stream = Resources.lines(resource.openStream(), charset)) {
            Iterator<String> iterator = stream.iterator();

            if (iterator.hasNext()) {
                int lines = Integer.parseInt(iterator.next());
                Map<String, String> map = new HashMap<>(lines);

                while (iterator.hasNext()) {
                    String next = iterator.next();
                    int i = next.indexOf(SEPARATOR);

                    if (i < 1) throw new RuntimeException("Wrong dictionary format");
                    String key = next.substring(i + 1);
                    String value = next.substring(0, i);
                    map.put(key, value);
                }
                return map;
            }
            return Collections.emptyMap();
        }
    }

}
