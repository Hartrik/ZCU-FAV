package cz.harag.ds.cv02.server;

import org.zeromq.ZMQ;

/**
 * @author Patrik Harag
 * @version 2020-12-04
 */
public class BankConnection {

    private final String bindAddress;
    private final String connectAddress;
    private ZMQ.Socket socket;

    public BankConnection(String bindAddress, String connectAddress) {
        this.bindAddress = bindAddress;
        this.connectAddress = connectAddress;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public String getConnectAddress() {
        return connectAddress;
    }

    public void setConnectSocket(ZMQ.Socket socket) {
        this.socket = socket;
    }

    public ZMQ.Socket getConnectSocket() {
        return socket;
    }
}
