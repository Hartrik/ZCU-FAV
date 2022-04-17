package cz.harag.psi.sp.ui;

/**
 * @author Patrik Harag
 * @version 2020-05-22
 */
public class ConnectionParameters {

    private final boolean ssl;
    private final String host;
    private final String port;

    public ConnectionParameters(String host, String port, boolean ssl) {
        this.ssl = ssl;
        this.host = host;
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }
}
