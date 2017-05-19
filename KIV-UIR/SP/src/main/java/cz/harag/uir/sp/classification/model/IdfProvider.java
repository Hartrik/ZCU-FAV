package cz.harag.uir.sp.classification.model;

import java.io.Serializable;

/**
 * Poskytuje Inverse document frequency - převrácená četnost slova ve všech dokumentech.
 *
 * @author Patrik Harag
 * @version 2017-04-29
 */
public interface IdfProvider extends Serializable {

    /**
     * Spočítá IDF slova.
     *
     * @param word slovo
     * @return idf
     */
    double countIdf(String word);

}
