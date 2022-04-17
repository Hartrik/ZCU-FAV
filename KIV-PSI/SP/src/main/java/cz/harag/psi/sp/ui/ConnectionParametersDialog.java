package cz.harag.psi.sp.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 *
 * @version 2020-05-22
 * @author Patrik Harag
 */
public class ConnectionParametersDialog extends Dialog<ConnectionParameters> {

    private CheckBox ssl = new CheckBox("SSL");
    private TextField host = new TextField();
    private TextField port = new TextField();

    public ConnectionParametersDialog() {
        init();
        defaults();
    }

    private void init() {
        setTitle("POP3 server");
        initModality(Modality.APPLICATION_MODAL);
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new ConnectionParameters(host.getText(), port.getText(), ssl.isSelected());
            }
            return null;
        });
    }

    private void defaults() {
        ssl.setSelected(true);
        host.setText("pop3.seznam.cz");
        port.setText("995");
    }

    private Node createContent() {
        VBox boxMain = new VBox(10,
                new Label("Host"),
                host,
                new Label("Port"),
                port,
                ssl
        );
        boxMain.setPadding(new Insets(20));
        boxMain.setPrefWidth(400);
        return boxMain;
    }

}