package cz.hartrik.puzzle.app;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @version 2017-09-28
 * @author Patrik Harag
 */
public class FrameStage extends Stage {

    private final FrameController frameController;

    public FrameStage(URL location) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root;
        try {
            root = fxmlLoader.load(location.openStream());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        this.frameController = fxmlLoader.getController();

        setScene(new Scene(root, 1100, 650));
        setOnCloseRequest((e) -> System.exit(0));
    }

    public FrameController getFrameController() {
        return frameController;
    }

}