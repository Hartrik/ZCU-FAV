package cz.hartrik.puzzle.net;

/**
 * Class that initializes connection.
 *
 * @author Patrik Harag
 * @version 2017-10-31
 */
public class ConnectionProvider {

    public static final int DEFAULT_PORT = 8076;
    public static final String DEFAULT_HOST = "localhost";

    public static Connection connect() throws Exception {
        return connect(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static Connection connect(String host, int port) throws Exception {
        Connection connection = new Connection(host, port);
        connection.connect();

        return connection;
    }

    public static Connection lazyConnect() {
        return lazyConnect(DEFAULT_HOST, DEFAULT_PORT);
    }

    public static Connection lazyConnect(String host, int port) {
        Connection connection = new Connection(host, port);
        return connection;
    }

}
