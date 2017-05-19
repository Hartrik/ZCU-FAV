package cz.harag.uir.sp.app;

import cz.harag.uir.sp.classification.model.*;

import java.util.function.Supplier;

/**
 * Výčtový typ dostupných modelů (parametrizačních algoritmů).
 *
 * @author Patrik Harag
 * @version 2017-05-03
 */
public enum ModelProvider {

    BINARY("bin", BinVectorModel::new),
    BINARY_REDUCED("bin-r", BinVectorReducedModel::new),
    TF("tf", TfVectorModel::new),
    TF_REDUCED("tf-r", TfVectorReducedModel::new),
    TF_IDF("tf-idf", TfIdfVectorModel::new),
    TF_IDF_REDUCED("tf-idf-r", TfIdfVectorReducedModel::new);

    private final String name;
    private final Supplier<Model<?>> supplier;

    ModelProvider(String name, Supplier<Model<?>> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    /**
     * Vrátí jméno modelu.
     *
     * @return jméno
     */
    public String getName() {
        return name;
    }

    /**
     * Vytvoří model.
     *
     * @return model
     */
    public Model<?> createModel() {
        return supplier.get();
    }

}
