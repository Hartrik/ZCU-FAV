package cz.zcu.kiv.pt.sp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jednoduchá implementace {@link SearchProvider} pomocí regulárních výrazů.
 *
 * @version 2016-09-30
 * @author Patrik Harag
 */
public class RegexSearch implements SearchProvider {

    private static final String PATTERN_START = "(^|\\W)(?<word>";
    private static final String PATTERN_END = ")($|\\W)";

    private final String text;

    public RegexSearch(String text) {
        this.text = text;
    }

    /**
     * Vyhledá v textu určitá slova.
     * Očekává se, že v hledaném slově nebudou žádné speciální symboly.
     *
     * @param word hledané slovo
     * @return výsledky
     */
    @Override
    public List<SearchResult> search(String word) {
        Pattern pattern = createPattern(word);
        Matcher matcher = pattern.matcher(text);

        List<SearchResult> results = new ArrayList<>();
        int fromIndex = 0;
        while (matcher.find(fromIndex)) {
            SearchResult result = new SearchResult(
                    matcher.start("word"), matcher.end("word"));

            fromIndex = result.getEnd();
            results.add(result);
        }

        return results;
    }

    private Pattern createPattern(String word) {
        // hledáme celé slovo, nikoli jeho část...
        String regex = PATTERN_START + regexEscape(word) + PATTERN_END;

        // nebere ohled na velikost znaků
        int flags = Pattern.UNICODE_CHARACTER_CLASS
                | Pattern.CASE_INSENSITIVE
                | Pattern.UNICODE_CASE;

        return Pattern.compile(regex, flags);
    }

    private String regexEscape(String text) {
        return text.replace(".", "\\.")
                   .replace("?", "\\?");  // další nás netrápí...
    }

}