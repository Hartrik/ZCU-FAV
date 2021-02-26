package cz.zcu.kiv.pt.sp.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Vstupní třída.
 *
 * @version 2016-10-23
 * @author Patrik Harag
 * @author Michal Fiala
 */
public class Main extends Application {

    private static final String FXML_FILE = "FXMLDocument.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(FXML_FILE));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Slovník");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}