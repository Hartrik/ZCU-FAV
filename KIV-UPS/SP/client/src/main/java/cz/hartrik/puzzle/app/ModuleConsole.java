
package cz.hartrik.puzzle.app;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @version 2017-11-12
 * @author Patrik Harag
 */
public class ModuleConsole implements ApplicationModule {

    private static final double HEIGHT = 300;
    private static final int MARGIN = 10;

    private final BooleanProperty visible = new SimpleBooleanProperty(false);

    @Override
    public void init(Application app) {
        StackPane rootPane = app.getController().getRootPane();

        VBox box = new VBox();
        box.getStyleClass().add("console-box");
        box.getChildren().addAll(createConsole());
        box.translateYProperty().bind(rootPane.heightProperty().subtract(HEIGHT));
        box.setVisible(false);

        addSlideHandlers(box, app);
        rootPane.getChildren().add(box);
    }

    private Node createConsole() {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(HEIGHT - (2 * MARGIN));
        VBox.setMargin(textArea, new Insets(MARGIN));

        Logger logger = Logger.getLogger("");
        SimpleFormatter formatter = new SimpleFormatter();
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                Platform.runLater(() -> {
                    textArea.appendText(formatter.format(record));
                });
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });

        return textArea;
    }

    private void addSlideHandlers(Node content, Application app) {
        Scene scene = app.getStage().getScene();

        visible.addListener((ov, o, n) -> {
            content.setVisible(n);
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.F12) {
                visible.set(!visible.getValue());
            }
        });
    }

}