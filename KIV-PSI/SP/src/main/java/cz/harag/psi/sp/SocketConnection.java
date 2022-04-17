package cz.harag.psi.sp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * Standard Java net sockets implementation of {@link Connection}.
 *
 * @author Patrik Harag
 * @version 2020-05-16
 */
public class SocketConnection implements Connection {

    public static final int TIMEOUT_MS = 3_000;

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private SocketConnection(Socket socket, String host, Integer port) throws IOException {
        socket.connect(new InetSocketAddress(host, port));
        socket.setSoTimeout(TIMEOUT_MS);
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Plain connection.
     *
     * @param host server host
     * @param port server port
     */
    public SocketConnection(String host, Integer port) throws IOException {
        this(new Socket(), host, port);
    }

    /**
     * SSL connection.
     *
     * @param host server host
     * @param port server port
     * @param sslSocketFactory socket factory
     */
    public SocketConnection(String host, Integer port, SSLSocketFactory sslSocketFactory) throws IOException {
        this(sslSocketFactory.createSocket(), host, port);
    }

    @Override
    public BufferedReader getReader() {
        return reader;
    }

    @Override
    public BufferedWriter getWriter() {
        return writer;
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
