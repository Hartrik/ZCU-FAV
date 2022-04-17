package cz.hartrik.puzzle.net;

/**
 * Used when the connection is terminated by client.
 *
 * @author Patrik Harag
 * @version 2017-12-09
 */
public class ConnectionTerminatedException extends RuntimeException {

    public ConnectionTerminatedException(String message) {
        super(message);
    }
}
