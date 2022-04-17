package cz.hartrik.puzzle.net.protocol;

/**
 * Server response to the LIN request.
 *
 * @author Patrik Harag
 * @version 2017-10-28
 */
public enum LogInResponse {

    OK,
    ALREADY_LOGGED,
    NAME_TOO_SHORT,
    NAME_TOO_LONG,
    UNSUPPORTED_CHARS,
    NAME_ALREADY_IN_USE,

    UNKNOWN;

    /**
     * Parses server response.
     *
     * @param string text
     * @return parsed response
     */
    public static LogInResponse parse(String string) {
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
