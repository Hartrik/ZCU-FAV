
package cz.hartrik.puzzle.app;

import cz.hartrik.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.stage.Stage;

/**
 * The main class.
 *
 * @version 2018-01-25
 * @author Patrik Harag
 */
public class Main extends javafx.application.Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String LOGGING_CONFIG = "/logging.properties";

    public static final String APP_NAME = "Online Puzzle";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "© 2018 Patrik Harag";
    public static final String APP_TITLE = APP_NAME + " " + APP_VERSION + " " + APP_AUTHOR;

    public static final String ICON = "puzzle-icon.png";

    private static FrameStage frameStage;

    @Override
    public void start(Stage stage) throws Exception {
        loadLoggingConfig();
        initGlobalExceptionHandler();
        processParameters(getParameters());

        setUserAgentStylesheet(STYLESHEET_MODENA);

        Application app = ApplicationBuilder.build(
                new ModuleConsole()
        );

        frameStage = app.getStage();
        frameStage.setTitle(APP_TITLE);
        frameStage.getIcons().add(Resources.image(ICON, Main.class));
        frameStage.setOnCloseRequest(e -> app.onClose());

        frameStage.show();
    }

    private void loadLoggingConfig() {
        InputStream inputStream = getClass().getResourceAsStream(LOGGING_CONFIG);

        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (IOException e) {
            LOGGER.severe("Could not load default logging.properties file");
            LOGGER.severe(e.getMessage());
        }
    }

    private void initGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString(); // stack trace as a string

            Logger.getGlobal().severe("Uncatched exception:\n" + stackTrace);
        });
    }

    private void processParameters(Parameters parameters) {
        Map<String, String> map = parameters.getNamed();

        String locale = map.getOrDefault("locale", null);
        if (locale != null) {
            try {
                Locale.setDefault(Locale.forLanguageTag(locale));
            } catch (Exception e) {
                LOGGER.severe(String.format("Cannot set locale to '%s'%n", locale));
            }
        }
    }

    static FrameStage getFrameStage() {
        return frameStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

}