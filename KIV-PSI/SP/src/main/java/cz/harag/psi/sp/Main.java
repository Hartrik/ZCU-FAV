package cz.harag.psi.sp;

import java.io.IOException;
import java.util.Locale;

import cz.harag.psi.sp.ui.AppMainUI;
import cz.harag.psi.sp.ui.ConnectionParameters;
import cz.harag.psi.sp.ui.ConnectionParametersDialog;
import cz.harag.psi.sp.ui.UserParameters;
import cz.harag.psi.sp.ui.UserParametersDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.net.ssl.SSLSocketFactory;

/**
 * Vstupní třída.
 *
 * @version 2020-05-22
 * @author Patrik Harag
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        POP3Client client = connect();
        if (client == null) {
            return;
        }

        new AppMainUI(stage, client);

        stage.show();
        stage.setOnCloseRequest(e -> {
            try {
                POP3ClientHelper.quit(client);
                client.close();
            } catch (Exception ex) {
                // nothing
            }
            Platform.exit();
        });
    }

    private Connection createConnection(ConnectionParameters parameters) throws IOException {
        String host = parameters.getHost();
        int port = Integer.parseInt(parameters.getPort());

        return (parameters.isSsl())
                ? new SocketConnection(host, port, (SSLSocketFactory) SSLSocketFactory.getDefault())
                : new SocketConnection(host, port);
    }

    private POP3Client connect() {
        POP3Client client = null;
        do {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    // ignore
                }
            }

            ConnectionParameters parameters = new ConnectionParametersDialog().showAndWait().orElse(null);
            if (parameters == null) {
                return null;
            }

            try {
                client = new POP3Client(createConnection(parameters));
            } catch (Exception e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Connection failed");
                alert.setContentText(e.toString());
                alert.showAndWait();
                continue;
            }

            if (authorize(client)) {
                return client;
            }

        } while (true);
    }

    private boolean authorize(POP3Client client) {
        do {
            String defaultUser = getParameters().getNamed().getOrDefault("user", "");
            String defaultPass = getParameters().getNamed().getOrDefault("pass", "");
            UserParameters userParameters = new UserParametersDialog(defaultUser, defaultPass).showAndWait().orElse(null);
            if (userParameters == null) {
                return false;
            }

            try {
                if (userParameters.getUser().isEmpty() || userParameters.getPass().isEmpty()) {
                    throw new IllegalArgumentException("Missing parameter");
                }
                POP3ClientHelper.authorize(client, userParameters.getUser(), userParameters.getPass());
                return true;
            } catch (Exception e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Authentication failed");
                alert.setContentText(e.toString());
                alert.showAndWait();
                if (e instanceof IOException) {
                    return false;
                }
            }
        } while (true);
    }

    /**
     * Vstupní metoda.
     *
     * @param args argumenty, první argument může být cesta k mapě
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }

}