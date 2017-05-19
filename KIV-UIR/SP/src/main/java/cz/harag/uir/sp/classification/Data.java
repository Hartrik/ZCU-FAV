package cz.harag.uir.sp.classification;

import java.io.Serializable;
import java.util.List;

/**
 * Reprezentace dokumentu po načtení (seznam slov + třída).
 *
 * @author Patrik Harag
 * @version 2017-04-05
 */
public class Data implements Serializable {

    private final String category;
    private final List<String> words;

    /**
     * Vytvoří novou instanci, třída neznámá.
     *
     * @param words slova
     */
    public Data(List<String> words) {
        this(null, words);
    }

    /**
     * Vytvoří novou instanci, třída neznámá.
     *
     * @param category kategorie
     * @param words slova
     */
    public Data(String category, List<String> words) {
        this.category = category;
        this.words = words;
    }

    /**
     * Vrátí kategorii.
     *
     * @return kategorie
     */
    public String getCategory() {
        return category;
    }

    /**
     * Vrátí seznam slov.
     *
     * @return slova
     */
    public List<String> getWords() {
        return words;
    }
}
