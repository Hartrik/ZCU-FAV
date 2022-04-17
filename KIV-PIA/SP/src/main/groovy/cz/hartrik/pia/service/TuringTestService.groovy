package cz.hartrik.pia.service

import java.util.function.Predicate

/**
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
interface TuringTestService {

    /**
     * Registers test.
     *
     * @param question
     * @param predicate
     */
    void register(String question, Predicate<String> predicate)

    /**
     * Returns random test.
     *
     * @return
     */
    TuringTest randomTest()

    /**
     * Tests answer.
     *
     * @param id
     * @param answer
     * @return true/false or null if test was not found
     */
    Boolean testAnswer(String id, String answer)

}
