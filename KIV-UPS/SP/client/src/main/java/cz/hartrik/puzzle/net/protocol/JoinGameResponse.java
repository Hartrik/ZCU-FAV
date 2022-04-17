package cz.hartrik.puzzle.net.protocol;

/**
 * Server response to the GJO request.
 *
 * @author Patrik Harag
 * @version 2017-10-15
 */
public enum JoinGameResponse {

    OK, CANNOT_JOIN, NO_PERMISSIONS, UNKNOWN;

    /**
     * Parses server response.
     *
     * @param string text
     * @return parsed response
     */
    public static JoinGameResponse parse(String string) {
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
