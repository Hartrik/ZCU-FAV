package cz.hartrik.pia

/**
 * Wrong input exception (3xx).
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
class WrongInputException extends RuntimeException {

    WrongInputException(String message) {
        super(message)
    }

    @Override
    String toString() {
        return "Bad request: " + message
    }
}
