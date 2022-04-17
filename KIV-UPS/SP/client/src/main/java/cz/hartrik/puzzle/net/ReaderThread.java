package cz.hartrik.puzzle.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Daemon thread that read data from socket.
 *
 * @author Patrik Harag
 * @version 2017-12-09
 */
public class ReaderThread extends Thread {

    private static final int YIELD_AFTER = 1024;
    private static final int MAX_CORRUPTED_MESSAGES = 50;
    private static final Logger LOGGER = Logger.getLogger(ReaderThread.class.getName());

    private final Map<String, Queue<MessageConsumer>> consumers;
    private final Consumer<Exception> onException;
    private volatile Reader reader;
    private volatile boolean close = false;
    private int corruptedMessages;

    public ReaderThread(Consumer<Exception> onException) {
        this.onException = onException;
        this.consumers = new ConcurrentHashMap<>();
        setDaemon(true);
    }

    /**
     * Init reader.
     *
     * @param inputStream stream
     * @param charset charset
     */
    public synchronized void initStream(InputStream inputStream, Charset charset) {
        if (reader != null)
            throw new IllegalStateException("Stream already initialized");

        InputStreamReader streamReader = new InputStreamReader(inputStream, charset);
        this.reader = new BufferedReader(streamReader);
    }

    @Override
    public void run() {
        if (reader == null)
            throw new IllegalStateException("Stream not initialized!");

        long yieldCounter = 0;
        try {
            StringBuilder builder = new StringBuilder();
            int c;

            while (!close && (c = reader.read()) != -1) {
                if (c == '|') {
                    if (builder.length() > 0) {
                        processMessage(builder.toString());
                        builder.setLength(0);
                    }
                } else {
                    builder.append((char) c);
                }

                yieldCounter++;
                if (yieldCounter > YIELD_AFTER) {
                    yieldCounter = 0;
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            onException.accept(e);
        }
    }

    private void processMessage(String data) {
        LOGGER.info("<-- " + data);

        if (data.length() < 3) {
            LOGGER.warning("Message too short: " + data);
        } else {
            String type = data.substring(0, 3);
            String content = data.substring(3);

            Queue<MessageConsumer> queue = this.consumers.get(type);
            if (queue != null && !queue.isEmpty()) {
                MessageConsumer consumer = queue.element();
                consumer.accept(content);

                if (consumer.isTemporary()) {
                    queue.poll();
                }
            } else {
                corruptedMessages++;
                LOGGER.warning("Unknown message type: " + type);

                if (corruptedMessages > MAX_CORRUPTED_MESSAGES) {
                    throw new ConnectionTerminatedException("Too many corrupted messages");
                }
            }
        }
    }

    /**
     * Adds consumer for given message type.
     *
     * @param type message type
     * @param contentConsumer callback
     */
    public void addConsumer(String type, MessageConsumer contentConsumer) {
        Queue<MessageConsumer> queue = consumers
                .getOrDefault(type, new ConcurrentLinkedQueue<>());

        queue.add(contentConsumer);
        consumers.put(type, queue);
    }

    /**
     * Sets consumer for given message type. Overrides current consumer(s).
     *
     * @param type message type
     * @param contentConsumer callback
     */
    public void setConsumer(String type, MessageConsumer contentConsumer) {
        Queue<MessageConsumer> queue = consumers
                .getOrDefault(type, new ConcurrentLinkedQueue<>());

        queue.clear();
        queue.add(contentConsumer);
        consumers.put(type, queue);
    }

    public void close() {
        this.close = true;
    }

}
