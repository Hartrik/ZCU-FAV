package cz.harag.uir.sp.classification.processing;

import cz.hartrik.common.Exceptions;
import cz.hartrik.common.io.Resources;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;

/**
 * Filtruje slova, která jsou ve slovníku.
 *
 * @author Patrik Harag
 * @version 2017-05-09
 */
public class StopWordsFilter implements Preprocessor.Filter {

    private final URL resource;
    private final Charset charset;
    private Set<String> dictionary;

    public StopWordsFilter(URL resource, Charset charset) {
        this.resource = resource;
        this.charset = charset;
    }

    private Set<String> loadDictionary() throws Exception {
        try (Stream<String> stream = Resources.lines(resource.openStream(), charset)) {
            Iterator<String> iterator = stream.iterator();

            if (iterator.hasNext()) {
                int lines = Integer.parseInt(iterator.next());
                Set<String> set = new HashSet<>(lines);

                while (iterator.hasNext()) {
                    set.add(iterator.next());
                }
                return set;
            }
            return Collections.emptySet();
        }
    }

    @Override
    public boolean test(String s) {
        Exceptions.unchecked(() -> {
            if (dictionary == null)
                dictionary = loadDictionary();
        });

        return !dictionary.contains(s);
    }

}
