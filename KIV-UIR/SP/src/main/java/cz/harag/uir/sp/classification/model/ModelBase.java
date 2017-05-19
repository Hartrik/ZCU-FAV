package cz.harag.uir.sp.classification.model;

import cz.harag.uir.sp.classification.Data;
import cz.harag.uir.sp.classification.processing.Postprocessor;
import cz.harag.uir.sp.classification.processing.Preprocessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pomocná abstraktní třída pro model.
 *
 * @author Patrik Harag
 * @version 2017-04-30
 */
public abstract class ModelBase<T extends FeatureVector<T>> implements Model<T> {

    protected transient List<Data> inputData = new LinkedList<>();
    private List<T> vectors;

    private Postprocessor postprocessor;
    private Preprocessor preprocessor;


    @Override
    public void setPreprocessor(Preprocessor p) {
        preprocessor = p;
    }

    @Override
    public void setPostprocessor(Postprocessor p) {
        postprocessor = p;
    }

    // fáze 1

    @Override
    public void add(Data data) {
        inputData.add(preprocess(data));
    }

    @Override
    public List<Data> getInputData() {
        return inputData;
    }

    // fáze 2

    @Override
    public void commit(boolean noVectors) {
        if (vectors != null)
            throw new IllegalStateException();

        if (postprocessor != null)
            postprocessor.processTrainingSet(inputData);

        if (!noVectors) {
            vectors = new ArrayList<>(inputData.size());
            for (Data data : inputData) {
                vectors.add(newInstance(data));
            }
        }
    }

    @Override
    public List<T> getFeatureVectors() {
        return vectors;
    }

    // fáze 3

    @Override
    public T createUnknownVector(Data data) {
        Data processed = preprocess(data);

        if (postprocessor != null)
            postprocessor.process(processed);

        return newInstance(processed);
    }

    // různé

    protected Data preprocess(Data data) {
        if (preprocessor != null) {
            List<String> words = preprocessor.apply(data.getWords().stream())
                    .collect(Collectors.toList());

            return new Data(data.getCategory(), words);
        }
        return data;
    }

    protected abstract T newInstance(Data data);

}
