package cz.harag.uir.sp.classification.processing;

import java.nio.charset.StandardCharsets;

/**
 * Obsahuje připravené pre-procesory.
 *
 * @author Patrik Harag
 * @version 2017-04-29
 */
public class Preprocessors {

    static final String LEMMATIZER_DICT_1 = "/lemma-1-cs.txt";
    static final String LEMMATIZER_DICT_2 = "/lemma-2-cs.txt";
    static final String STOP_WORDS_DICT = "/stop-words-cs.txt";

    // --- filters

    public static final Preprocessor.Filter NUMBER_FILTER = s -> {
        // must starts with a digit
        return !(!s.isEmpty() && Character.isDigit(s.charAt(0)));
    };

    public static final Preprocessor.Filter SINGLE_LETTER_FILTER = s -> {
        return s.length() > 1;
    };

    public static final Preprocessor.Filter STOP_WORDS_FILTER = new StopWordsFilter(
            System.class.getResource(STOP_WORDS_DICT),
            StandardCharsets.UTF_8
    );

    // --- mappers

    public static final Preprocessor.Mapper IGNORE_CASE = String::toLowerCase;

    public static final Preprocessor.Mapper UNIFY_NUMBERS = s -> {
        return NUMBER_FILTER.test(s) ? s : "999";
    };

    public static final Preprocessor.Mapper LEMMATIZER_1 = new Lemmatizer(
            System.class.getResource(LEMMATIZER_DICT_1),
            StandardCharsets.UTF_8
    );

    public static final Preprocessor.Mapper LEMMATIZER_2 = new Lemmatizer(
            System.class.getResource(LEMMATIZER_DICT_2),
            StandardCharsets.UTF_8
    );

    // ---

    public static final Preprocessor DEFAULT_PREPROCESSOR = stream -> {
        return stream
                .map(UNIFY_NUMBERS)
                // .filter(Preprocessor.NUMBER_FILTER)
                .filter(SINGLE_LETTER_FILTER)
                .map(IGNORE_CASE)
                .map(LEMMATIZER_2)
                .map(LEMMATIZER_1)
                .filter(STOP_WORDS_FILTER)
                ;
    };

}