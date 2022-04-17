package cz.hartrik.puzzle.net;

import cz.hartrik.common.Exceptions;
import cz.hartrik.puzzle.net.protocol.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides high level communication with a server.
 *
 * @author Patrik Harag
 * @version 2017-12-09
 */
public class Connection implements AutoCloseable {

    private static final Charset CHARSET = StandardCharsets.US_ASCII;


    private final String host;
    private final int port;
    private boolean connected;

    private Socket socket;

    private ReaderThread reader;
    private WriterThread writer;

    private volatile Consumer<Exception> onConnectionLost;
    private volatile Exception exception;

    Connection(String host, int port) {
        this.host = host;
        this.port = port;
        this.reader = new ReaderThread(this::onException);
        this.writer = new WriterThread(this::onException);
    }

    /**
     * Start connection.
     */
    synchronized void connect() throws IOException {
        if (connected) return;  // already connected
        this.connected = true;

        this.exception = null;
        this.socket = new Socket(host, port);

        this.reader.initStream(socket.getInputStream(), CHARSET);
        this.writer.initStream(socket.getOutputStream(), CHARSET);

        reader.start();
        writer.start();
    }

    void setOnConnectionLost(Consumer<Exception> onConnectionLost) {
        this.onConnectionLost = onConnectionLost;
    }

    /**
     * Adds consumer for given message type.
     *
     * @param type message type
     * @param consumer callback
     */
    public void addConsumer(String type, MessageConsumer consumer) {
        this.reader.addConsumer(type, consumer);
    }

    /**
     * Sets consumer for given message type. Overrides current consumer(s).
     *
     * @param type message type
     * @param consumer callback
     */
    public void setConsumer(String type, MessageConsumer consumer) {
        this.reader.setConsumer(type, consumer);
    }

    private void onException(Exception e) {
        if (exception != null) return;  // exception from second thread...

        this.exception = e;
        e.printStackTrace();
        Exceptions.silent(this::closeNow);

        if (onConnectionLost != null)
            onConnectionLost.accept(e);
    }

    private void send(String content) throws Exception {
        if (exception != null)
            throw exception;

        // TODO: escaping
        writer.send(content);
    }

    void sendMessage(String type, String content) throws Exception {
        send(String.format("|%s%s|", type, content));
    }

    Future<String> sendMessageAndHook(String type, String content) throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        reader.addConsumer(type, MessageConsumer.temporary(future::complete));

        send(String.format("|%s%s|", type, content));

        return future;
    }

    void sendRaw(String data) throws Exception {
        send(String.format("|%s|", data));
    }

    public void sendPing() throws Exception {
        connect();

        sendMessage("PIN", "");
    }

    public Future<LogInResponse> sendLogIn(String nick) throws Exception {
        connect();

        CompletableFuture<LogInResponse> future = new CompletableFuture<>();
        reader.addConsumer("LIN", MessageConsumer.temporary(response -> {
            future.complete(LogInResponse.parse(response));
        }));

        sendMessage("LIN", nick);

        return future;
    }

    public Future<LogOutResponse> sendLogOut() throws Exception {
        connect();

        CompletableFuture<LogOutResponse> future = new CompletableFuture<>();
        reader.addConsumer("LOF", MessageConsumer.temporary(response -> {
            future.complete(LogOutResponse.parse(response));
        }));

        sendMessage("LOF", "");

        return future;
    }

    public Future<NewGameResponse> sendNewGame(int w, int h) throws Exception {
        connect();

        CompletableFuture<NewGameResponse> future = new CompletableFuture<>();
        reader.addConsumer("GNW", MessageConsumer.temporary(response -> {
            future.complete(NewGameResponse.parse(response));
        }));

        sendMessage("GNW", w + "," + h);

        return future;
    }

    public Future<JoinGameResponse> sendJoinGame(int gameID) throws Exception {
        connect();

        CompletableFuture<JoinGameResponse> future = new CompletableFuture<>();
        reader.addConsumer("GJO", MessageConsumer.temporary(response -> {
            future.complete(JoinGameResponse.parse(response));
        }));

        sendMessage("GJO", "" + gameID);

        return future;
    }

    public Future<GameStateResponse> sendGameStateUpdate(int gameID) throws Exception {
        connect();

        CompletableFuture<GameStateResponse> future = new CompletableFuture<>();
        reader.addConsumer("GST", MessageConsumer.temporary(response -> {
            future.complete(GameStateResponse.parse(response));
        }));

        sendMessage("GST", "" + gameID);

        return future;
    }

    public Future<GameListResponse> sendGameList() throws Exception {
        connect();

        CompletableFuture<GameListResponse> future = new CompletableFuture<>();
        reader.addConsumer("GLI", MessageConsumer.temporary(response -> {
            future.complete(GameListResponse.parse(response));
        }));

        sendMessage("GLI", "");

        return future;
    }

    public Future<Set<String>> sendPlayerList(int gameID) throws Exception {
        connect();

        CompletableFuture<Set<String>> future = new CompletableFuture<>();
        reader.addConsumer("GPL", MessageConsumer.temporary(response -> {
            Set<String> players = Stream.of(response.split(","))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            future.complete(players);
        }));

        sendMessage("GPL", "" + gameID);

        return future;
    }

    public Future<GenericResponse> sendGameAction(List<GameStateResponse.Piece> pieces) throws Exception {
        connect();

        CompletableFuture<GenericResponse> future = new CompletableFuture<>();
        reader.addConsumer("GAC", MessageConsumer.temporary(response -> {
            future.complete(GenericResponse.parse(response));
        }));

        StringBuilder sb = new StringBuilder();
        for (GameStateResponse.Piece piece : pieces) {
            sb.append(piece.getId()).append(',')
                    .append(piece.getX()).append(',')
                    .append(piece.getY()).append(';');
        }
        sendMessage("GAC", sb.toString());

        return future;
    }

    public Future<Void> sendLeaveGame() throws Exception {
        connect();

        CompletableFuture<Void> future = new CompletableFuture<>();
        reader.addConsumer("GOF", MessageConsumer.temporary(response -> {
            future.complete(null);
        }));

        sendMessage("GOF", "");

        return future;
    }

    /**
     * Closes connection, blocks.
     */
    @Override
    public synchronized void close() throws Exception {
        if (exception != null || !connected) {
            // closed already
        } else {
            sendMessage("BYE", "");
            closeNow();

            if (exception != null) {
                // exception occurred
                throw exception;
            }
        }
    }

    private void closeNow() throws Exception {
        writer.close();
        reader.close();

        Exceptions.silent(() -> writer.join(500));
        Exceptions.silent(() -> reader.join(500));

        if (socket != null) socket.close();
    }
}
