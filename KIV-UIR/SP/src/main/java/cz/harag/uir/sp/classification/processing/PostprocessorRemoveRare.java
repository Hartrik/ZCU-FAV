package cz.harag.uir.sp.classification.processing;

import cz.harag.uir.sp.Config;
import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Odebere slova, která mají počet výskytů menší než minimální počet.
 *
 * @author Patrik Harag
 * @version 2017-04-09
 */
public class PostprocessorRemoveRare implements Postprocessor {

    private static final double MIN_OCCURRENCES = 2;

    private List<String> toRemove = new LinkedList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTrainingSet(List<Data> list) {
        Map<String, Integer> words = Utils.frequencies(Utils.words(list));

        words.entrySet().forEach(e -> {
            if (e.getValue() < MIN_OCCURRENCES)
                remove(e.getKey());
        });

        if (Config.DEBUG)
            System.out.println("PostprocessorRemoveRare /removed: " + toRemove.size());
    }

    private void remove(String w) {
        toRemove.add(w);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Data data) {
        data.getWords().removeAll(toRemove);
    }

}
