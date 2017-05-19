package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;

/**
 * Model používající TF-IDF reprezentaci.
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public class TfIdfVectorModel extends ModelBase<TfIdfVector> {

    private IdfProvider idfProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    protected TfIdfVector newInstance(Data data) {
        return new TfIdfVector(data, idfProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit(boolean noVectors) {
        idfProvider = IdfProviderImpl.of(inputData);
        super.commit(noVectors);
    }
}
