package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;

/**
 * Model používající binární reprezentaci dokumentů.
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public class BinVectorModel extends ModelBase<BinVector> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected BinVector newInstance(Data data) {
        return new BinVector(data);
    }
}
