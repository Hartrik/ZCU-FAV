package cz.harag.uir.sp.classification.processing;

import cz.harag.uir.sp.classification.Data;

import java.util.List;

/**
 * Slouží ke skládání post-procesorů.
 *
 * @author Patrik Harag
 * @version 2017-04-09
 */
public class PostprocessorCombined implements Postprocessor {

    private final Postprocessor[] postprocessors;

    public PostprocessorCombined(Postprocessor... postprocessors) {
        this.postprocessors = postprocessors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTrainingSet(List<Data> list) {
        if (postprocessors.length == 0) return;

        for (Postprocessor postprocessor : postprocessors) {
            postprocessor.processTrainingSet(list);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Data data) {
        if (postprocessors.length == 0) return;

        for (Postprocessor postprocessor : postprocessors) {
            postprocessor.process(data);
        }
    }
}
