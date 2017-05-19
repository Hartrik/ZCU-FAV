package cz.harag.uir.sp.classification.processing;

import cz.harag.uir.sp.Config;
import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.Utils;

import java.util.*;

/**
 * Odebere slova, která jsou obsažena ve všech třídách.
 *
 * @author Patrik Harag
 * @version 2017-04-09
 */
public class PostprocessorRemoveInEveryClass implements Postprocessor {

    private Set<String> toRemove = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTrainingSet(List<Data> list) {
        if (list.isEmpty()) return;

        // vezme všechny slova z jedné třídy a potom od nich postupně odebírá
        // slova, která nejsou obsažena v jiných třídách
        // zbydou slova, která jsou obsažena ve všech třídách (libovolně krát)

        Map<String, List<Data>> byCategory = Utils.groupByCategory(list);
        Iterator<List<Data>> iterator = byCategory.values().iterator();

        Set<String> commonWords = Utils.uniqueWords(iterator.next());

        while (iterator.hasNext()) {
            Set<String> set = Utils.uniqueWords(iterator.next());
            commonWords.retainAll(set);

            if (commonWords.isEmpty())
                break;
        }

        toRemove = commonWords;
        list.forEach(d -> d.getWords().removeAll(toRemove));

        if (Config.DEBUG)
            System.out.println("PostprocessorRemoveInEveryClass /removed: " + toRemove.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Data data) {
        data.getWords().removeAll(toRemove);
    }
}
