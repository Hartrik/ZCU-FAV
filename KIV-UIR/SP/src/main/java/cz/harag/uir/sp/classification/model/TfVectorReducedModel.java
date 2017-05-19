package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Utils;

/**
 * Model používající redukovanou frekvenční reprezentaci dokumentů.
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public class TfVectorReducedModel extends TfVectorModel {

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(boolean noVectors) {
        inputData = Utils.reduce(inputData);
        super.commit(noVectors);
    }

}
