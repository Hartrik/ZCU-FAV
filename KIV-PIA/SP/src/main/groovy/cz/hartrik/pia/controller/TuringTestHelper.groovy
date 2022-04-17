package cz.hartrik.pia.controller

import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.service.TuringTestService

/**
 * Helper class for working with turing test.
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
class TuringTestHelper {

    static void testRequest(TuringTestService turingTestService, object) {
        def test = turingTestService.testAnswer(
                object.turingTestQuestionId, object.turingTestAnswer)
        if (test == null) {
            throw new WrongInputException("Anti-robot test has expired")
        } else if (!test) {
            throw new WrongInputException("Incorrect anti-robot test answer")
        }
    }

}
