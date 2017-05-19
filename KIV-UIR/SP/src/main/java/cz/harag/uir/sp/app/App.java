package cz.harag.uir.sp.app;

import cz.harag.uir.sp.classification.Classifier;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Třída zavádějící JavaFX aplikaci.
 *
 * @version 2017-04-30
 * @author Patrik Harag
 */
public class App extends Application {

    private static final String FXML_FILE = "FXMLDocument.fxml";

    private static Classifier<?> classifier;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(FXML_FILE));

        FXMLDocumentController.instance.classifier = classifier;

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Document classifier");
        stage.setWidth(700);
        stage.setHeight(400);
        stage.show();
    }

    /**
     * Spustí JavaFX aplikaci - robrazí GUI.
     *
     * @param c klasifikátor
     */
    public static void launchApp(Classifier<?> c) {
        classifier = c;
        App.launch();
    }

}