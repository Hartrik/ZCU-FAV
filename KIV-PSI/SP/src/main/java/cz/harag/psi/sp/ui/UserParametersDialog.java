package cz.harag.psi.sp.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 *
 * @version 2020-05-22
 * @author Patrik Harag
 */
public class UserParametersDialog extends Dialog<UserParameters> {

    private TextField user = new TextField();
    private PasswordField pass = new PasswordField();

    public UserParametersDialog(String defaultUser, String defaultPass) {
        user.setText(defaultUser);
        pass.setText(defaultPass);
        init();
    }

    private void init() {
        setTitle("User credentials");
        initModality(Modality.APPLICATION_MODAL);
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new UserParameters(user.getText(), pass.getText());
            }
            return null;
        });
    }

    private Node createContent() {
        VBox boxMain = new VBox(10,
                new Label("User"),
                user,
                new Label("Pass"),
                pass
        );
        boxMain.setPadding(new Insets(20));
        boxMain.setPrefWidth(400);
        return boxMain;
    }

}