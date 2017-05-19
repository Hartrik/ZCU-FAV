package cz.harag.uir.sp.classification.processing;

import cz.harag.uir.sp.classification.Data;

import java.io.Serializable;
import java.util.List;

/**
 * rozhraní pro třídu která, podobně jako Preprocessor, předzpracovává dokumenty.
 * Rozdíl je v tom, že Postprocessor je aplikován až po načtení všech dokumentů.
 *
 * @author Patrik Harag
 * @version 2017-04-29
 */
public interface Postprocessor extends Serializable {

    /**
     * Zpracuje trénovací množinu.
     *
     * @param list seznam vstupních dokumentů
     */
    void processTrainingSet(List<Data> list);

    /**
     * Zpracuje dokument (testovací data).
     *
     * @param data dokument
     */
    void process(Data data);

}
