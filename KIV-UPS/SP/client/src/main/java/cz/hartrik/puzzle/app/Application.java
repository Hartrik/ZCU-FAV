package cz.hartrik.puzzle.app;

import cz.hartrik.puzzle.net.ConnectionHolder;
import cz.hartrik.puzzle.app.page.ErrorPage;
import cz.hartrik.puzzle.app.page.LogInPage;
import cz.hartrik.puzzle.app.page.Page;
import cz.hartrik.puzzle.net.ConnectionTerminatedException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @version 2017-12-09
 * @author Patrik Harag
 */
public class Application {

    public static final int DEFAULT_TIMEOUT = 4000;  // ms

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    private final FrameStage frameStage;
    private final FrameController controller;
    private final Page defaultPage;

    private ConnectionHolder connection;

    public Application(FrameStage frameStage, FrameController controller) {
        this.frameStage = frameStage;
        this.controller = controller;
        this.defaultPage = new LogInPage(this);

        setActivePage(defaultPage);
    }

    public FrameStage getStage() {
        return frameStage;
    }

    public FrameController getController() {
        return controller;
    }

    public void setActivePage(Page newPage) {
        Page currentPage = getController().getActivePage();

        // filter some errors...
        // mostly delayed errors from other threads after connection lost
        if (newPage instanceof ErrorPage) {
            String msg = ((ErrorPage) newPage).getMessage();

            // secondary error
            if (currentPage instanceof ErrorPage) {
                LOGGER.warning("Secondary error suppressed - " + msg);
                return;
            }

            // delayed error
            if (currentPage instanceof LogInPage) {
                LOGGER.warning("Delayed error suppressed - " + msg);
                return;
            }
        }

        if (Platform.isFxApplicationThread())
            getController().setActivePage(newPage);
        else
            Platform.runLater(() -> getController().setActivePage(newPage));
    }

    public void setConnection(ConnectionHolder connection) {
        this.connection = connection;

        connection.setOnConnectionLost((e) -> {
            String msg;
            if (e instanceof ConnectionTerminatedException) {
                msg = "Connection terminated - " + e.getMessage();
            } else {
                msg = "Connection lost";
            }

            Page errorPage = new ErrorPage(this, defaultPage, msg);
            setActivePage(errorPage);
            this.connection = null;
        });
    }

    public ConnectionHolder getConnection() {
        return connection;
    }

    public void logException(String msg, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString(); // stack trace as a string

        LOGGER.warning(msg + "\n" + stackTrace);
    }

    public void log(String msg) {
        LOGGER.warning(msg);
    }

    void onClose() {
        controller.getActivePage().onClose();

        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                logException("While closing:", e);
            }
        }
    }

}