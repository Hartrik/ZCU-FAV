package cz.zcu.kiv.pt.sp;

import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Slouží k vytváření slovníku z nějakého obecného textu.
 *
 * @version 2016-09-28
 * @author Patrik Harag
 */
public class DictionaryCreator {

    private final Supplier<Dictionary> dictionaryFactory;

    public DictionaryCreator() {
        this.dictionaryFactory = HashSetDictionary::new;
    }

    public DictionaryCreator(Supplier<Dictionary> dictionaryFactory) {
        this.dictionaryFactory = dictionaryFactory;
    }

    /**
     * Vytvoří slovník z textu. Bere v úvahu čárky, tečky atd...
     *
     * @param text vstupní text
     * @return slovník
     */
    public Dictionary createDictionary(String text) {
        Dictionary dict = dictionaryFactory.get();
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        for (String word : pattern.split(text)) {
            if (!word.isEmpty()) {
                dict.add(word.toLowerCase());
            }
        }
        return dict;
    }

}
