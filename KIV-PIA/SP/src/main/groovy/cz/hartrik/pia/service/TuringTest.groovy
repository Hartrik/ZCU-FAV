package cz.hartrik.pia.service

import java.util.function.Predicate

/**
 *
 * @version 2018-11-20
 * @author Patrik Harag
 */
interface TuringTest extends Predicate<String> {

    String getId()

    String getQuestion()

    @Override
    boolean test(String response)

}
