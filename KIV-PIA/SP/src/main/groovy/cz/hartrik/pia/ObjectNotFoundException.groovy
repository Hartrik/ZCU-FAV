package cz.hartrik.pia

/**
 * Exception when object (from model) is not found.
 * (e.g. user by given id)
 *
 * @version 2018-11-17
 * @author Patrik Harag
 */
class ObjectNotFoundException extends WrongInputException {

    ObjectNotFoundException(String message) {
        super(message)
    }

}
