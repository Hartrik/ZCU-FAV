package cz.hartrik.puzzle.net.protocol;

/**
 * Server response to the GAC request.
 *
 * @author Patrik Harag
 * @version 2017-10-28
 */
public enum GenericResponse {

    OK,
    NO_PERMISSIONS,
    WRONG_FORMAT,
    LOGICAL_ERROR,

    UNKNOWN;

    /**
     * Parses server response.
     *
     * @param string text
     * @return parsed response
     */
    public static GenericResponse parse(String string) {
        try {
            int i = Integer.parseInt(string);
            if (i >= 0 && i < values().length) {
                return values()[i];
            } else {
                return UNKNOWN;
            }

        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
    }
}
