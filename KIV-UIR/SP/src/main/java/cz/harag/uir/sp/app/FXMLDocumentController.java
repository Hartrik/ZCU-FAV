package cz.harag.uir.sp.app;

import cz.harag.uir.sp.classification.Classifier;
import cz.harag.uir.sp.classification.Data;
import cz.hartrik.common.ui.javafx.ExceptionDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * FXML kontroler (obsluha GUI).
 *
 * @version 2017-04-30
 * @author Patrik Harag
 */
public class FXMLDocumentController implements Initializable {

    public static FXMLDocumentController instance;

    Classifier<?> classifier;

    @FXML
    private TextArea input;

    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        input.setOnKeyTyped(e -> label.setText("?"));

        instance = this;
    }

    @FXML
    private void classify() {
        Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        List<String> list = pattern.splitAsStream(input.getText())
                .filter(word -> !word.isEmpty())
                .collect(Collectors.toList());

        Data data = new Data(list);

        try {
            String clazz = classifier.classify(data);
            label.setText(clazz);

        } catch (Exception e) {
            showErrorAlert(e);
        }
    }

    private void showErrorAlert(Exception e) {
        Alert alert = new ExceptionDialog(e);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Došlo k neočekávané chybě");
        alert.initOwner(getWindow());
        alert.showAndWait();
    }

    private Window getWindow() {
        return input.getScene().getWindow();
    }

}
