package cz.hartrik.puzzle.net.protocol;

/**
 * Server response to the GNW request.
 *
 * @author Patrik Harag
 * @version 2017-10-15
 */
public class NewGameResponse {

    public enum Status {
        OK, WRONG_FORMAT, WRONG_SIZE, NO_PERMISSIONS, UNKNOWN;
    }


    private final Status status;
    private final int gameID;

    public NewGameResponse(int gameID) {
        this.status = Status.OK;
        this.gameID = gameID;
    }

    public NewGameResponse(Status status) {
        this.status = status;
        this.gameID = -1;
    }

    public Status getStatus() {
        return status;
    }

    public int getGameID() {
        return gameID;
    }

    /**
     * Parses server response.
     *
     * @param string text
     * @return parsed response
     */
    public static NewGameResponse parse(String string) {
        try {
            int i = Integer.parseInt(string);
            if (i < 0) {
                int mapped = -i;
                if (mapped < Status.values().length) {
                    return new NewGameResponse(Status.values()[mapped]);
                } else {
                    return new NewGameResponse(Status.UNKNOWN);
                }
            } else {
                return new NewGameResponse(i);
            }

        } catch (NumberFormatException e) {
            return new NewGameResponse(Status.UNKNOWN);
        }
    }

}
