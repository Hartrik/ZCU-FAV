package cz.hartrik.puzzle.app.page;

import cz.hartrik.common.Exceptions;
import cz.hartrik.puzzle.app.Application;
import cz.hartrik.puzzle.net.Connection;
import cz.hartrik.puzzle.net.ConnectionHolder;
import cz.hartrik.puzzle.net.ConnectionProvider;
import cz.hartrik.puzzle.net.protocol.LogInResponse;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * @author Patrik Harag
 * @version 2017-10-31
 */
public class LogInPage implements Page {

    private final Application application;
    private final Node node;

    private TextField hostTextField;
    private TextField portTextField;
    private TextField userTextField;

    public LogInPage(Application application) {
        this.application = application;
        this.node = createNode();
    }

    private Node createNode() {
        VBox box = new VBox();
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);

        Text title = new Text("Online Puzzle");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 32));

        VBox dummy = new VBox();
        dummy.setMinHeight(50);

        Label hostLabel = new Label("Host");

        hostTextField = new TextField();
        hostTextField.setText(ConnectionProvider.DEFAULT_HOST);
        hostTextField.setMaxWidth(200);
        hostTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                tryApply();
            }
        });

        Label portLabel = new Label("Port");

        portTextField = new LimitedTextField("[0-9]*", 5);
        portTextField.setText("" + ConnectionProvider.DEFAULT_PORT);
        portTextField.setMaxWidth(200);
        portTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                tryApply();
            }
        });

        Label userLabel = new Label("Nick");

        userTextField = new LimitedTextField("[0-9a-zA-Z]*", 12);
        userTextField.setMaxWidth(200);
        userTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                tryApply();
            }
        });

        box.getChildren().setAll(
                title,
                dummy,
                hostLabel, hostTextField,
                portLabel, portTextField,
                userLabel, userTextField
        );
        return box;
    }

    @Override
    public Node getNode() {
        return node;
    }

    private void tryApply() {
        String host = hostTextField.getText();
        String port = portTextField.getText();
        String name = userTextField.getText();

        if (!name.isEmpty() && !host.isEmpty() && !port.isEmpty())
            apply(host, Integer.parseInt(port), name);
    }

    private void apply(String host, int port, String name) {
        application.setActivePage(new LoadingPage());

        Connection connection = ConnectionProvider.lazyConnect(host, port);
        ConnectionHolder holder = new ConnectionHolder(connection);
        application.setConnection(holder);

        holder.async(c -> {
            Future<LogInResponse> future = c.sendLogIn(name);
            LogInResponse response = future.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

            if (response != LogInResponse.OK) {
                Exceptions.silent(c::close);
                throw new RuntimeException("Error when logging in: " + response);
            } else {
                Page page = new MenuPage(application, this);
                application.setActivePage(page);
            }

        }, this::onError);
    }

    private void onError(Exception e) {
        application.logException("Error", e);
        Page errorPage = new ErrorPage(application, this, e.toString());
        application.setActivePage(errorPage);
    }

    @Override
    public void onShow() {
        userTextField.requestFocus();
    }
}
