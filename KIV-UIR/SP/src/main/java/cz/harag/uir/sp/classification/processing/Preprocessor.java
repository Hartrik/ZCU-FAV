package cz.harag.uir.sp.classification.processing;

import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Rozhraní pro třídu která předzpracovává dokumenty.
 * Dostává proud slov, které je možné filtrovat, modifikovat atd.
 *
 * @author Patrik Harag
 * @version 2017-04-29
 */
public interface Preprocessor extends UnaryOperator<Stream<String>>, Serializable {

    interface Filter extends Predicate<String>, Serializable {

    }

    interface Mapper extends UnaryOperator<String>, Serializable {

    }

}
