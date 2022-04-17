package cz.hartrik.puzzle.net;

import cz.hartrik.common.Exceptions;
import java.util.function.Consumer;

/**
 * Holds connection, provides higher abstraction.
 *
 * @author Patrik Harag
 * @version 2017-12-07
 */
public class ConnectionHolder implements AutoCloseable {

    public interface Command {
        void apply(Connection c) throws Exception;
    }

    private static volatile int ID = 0;

    private final Connection connection;

    public ConnectionHolder(Connection connection) {
        this.connection = connection;

        connection.addConsumer("PIN", MessageConsumer.persistent(s -> {
            Exceptions.silent(connection::sendPing);
        }));
    }

    public void setOnConnectionLost(Consumer<Exception> onConnectionLost) {
        connection.setOnConnectionLost(onConnectionLost);
    }

    public Thread async(Command command, Consumer<Exception> onError) {
        Thread thread = new Thread(() -> {
            try {
                command.apply(connection);
            } catch (Exception e) {
                onError.accept(e);
            }
        }, "ConnectionHolder/Command/" + (++ID));
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public Thread asyncFinally(Command command, Consumer<Exception> after) {
        Thread thread = new Thread(() -> {
            Exception exception = null;
            try {
                command.apply(connection);
            } catch (Exception e) {
                exception = e;
            } finally {
                after.accept(exception);
            }
        }, "ConnectionHolder/Command/" + (++ID));
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    /**
     * Adds consumer for given message type.
     *
     * @param type message type
     * @param consumer callback
     */
    public void addConsumer(String type, MessageConsumer consumer) {
        connection.addConsumer(type, consumer);
    }

    /**
     * Sets consumer for given message type. Overrides current consumer(s).
     *
     * @param type message type
     * @param consumer callback
     */
    public void setConsumer(String type, MessageConsumer consumer) {
        connection.setConsumer(type, consumer);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

}
