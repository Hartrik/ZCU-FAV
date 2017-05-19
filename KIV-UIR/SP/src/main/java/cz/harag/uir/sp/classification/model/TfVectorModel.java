package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;

/**
 * Model používající frekvenční reprezentaci dokumentů.
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public class TfVectorModel extends ModelBase<TfVector> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected TfVector newInstance(Data data) {
        return new TfVector(data);
    }
}
