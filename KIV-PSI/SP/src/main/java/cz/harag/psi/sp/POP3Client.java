package cz.harag.psi.sp;

import java.io.IOException;

/**
 * Low level API for working with POP3 protocol.
 *
 * @author Patrik Harag
 * @version 2020-05-22
 */
public class POP3Client implements AutoCloseable {

    private final Connection connection;

    public POP3Client(Connection connection) throws IOException {
        this.connection = connection;
        readResponse();  // should just pass
    }

    public synchronized String sendAndExpectSingleLine(String command, String arg) throws IOException {
        send(command, arg);

        return readResponse();
    }

    public synchronized String sendAndExpectMultiLine(String command, String arg) throws IOException {
        send(command, arg);

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while (!(line = readResponse()).equals(".")) {
            line = line.replaceFirst("^\\.\\.", ".");
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }

    private void send(String command, String arg) throws IOException {
        String data = (arg != null) ? command + " " + arg : command;
        LoggingProvider.logRequest(data);
        connection.getWriter().write(data);
        connection.getWriter().write("\r\n");
        connection.getWriter().flush();
    }

    private String readResponse() throws IOException, POP3Exception {
        String response = connection.getReader().readLine();
        if (response.startsWith("-ERR")) {
            throw new POP3Exception(response);
        }
        LoggingProvider.logResponse(response);
        return response;
    }

    @Override
    public synchronized void close() throws Exception {
        try {
            connection.close();
        } finally {
            LoggingProvider.log("Connection closed");
        }
    }
}
