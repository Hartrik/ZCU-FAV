package cz.hartrik.puzzle.net.protocol;

/**
 * Server response to the LOF request.
 *
 * @author Patrik Harag
 * @version 2017-10-10
 */
public enum LogOutResponse {

    OK,
    IN_GAME,

    UNKNOWN;

    /**
     * Parses server response.
     *
     * @param string text
     * @return parsed response
     */
    public static LogOutResponse parse(String string) {
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
