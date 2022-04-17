package cz.hartrik.puzzle.net;

import cz.hartrik.puzzle.app.Application;
import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Daemon thread that write data into socket.
 *
 * @author Patrik Harag
 * @version 2017-11-12
 */
public class WriterThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(WriterThread.class.getName());

    private final Consumer<Exception> onException;
    private final BlockingQueue<String> queue;
    private volatile Writer writer;
    private volatile boolean close = false;

    public WriterThread(Consumer<Exception> onException) {
        this.onException = onException;
        this.queue = new LinkedBlockingQueue<>();
        setDaemon(true);
    }

    public synchronized void initStream(OutputStream outputStream, Charset charset) {
        if (writer != null)
            throw new IllegalStateException("Stream already initialized");

        this.writer = new OutputStreamWriter(outputStream, charset);
    }

    @Override
    public void run() {
        if (writer == null)
            throw new IllegalStateException("Stream not initialized!");

        try {
            while (!close || !queue.isEmpty()) {
                String data = queue.poll(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (data != null) {
                    writeData(data);
                }
            }
        } catch (Exception e) {
            onException.accept(e);
        }
    }

    private void writeData(String data) throws IOException {
        writer.write(data);
        writer.flush();

        LOGGER.info("--> " + data);
    }

    public void send(String data) {
        if (!queue.add(data))
            throw new RuntimeException();
    }

    public void close() {
        this.close = true;
    }

}
