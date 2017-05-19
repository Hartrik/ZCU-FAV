package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Utils;

/**
 * Model používající redukovanou binární reprezentaci dokumentů.
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public class BinVectorReducedModel extends BinVectorModel {

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(boolean noVectors) {
        inputData = Utils.reduce(inputData);
        super.commit(noVectors);
    }

}
