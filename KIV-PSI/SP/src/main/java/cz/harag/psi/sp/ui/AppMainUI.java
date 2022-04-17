package cz.harag.psi.sp.ui;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import cz.harag.psi.sp.POP3Client;
import cz.harag.psi.sp.POP3ClientHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.commons.mail.util.MimeMessageParser;

/**
 *
 * @version 2020-05-24
 * @author Patrik Harag
 */
public class AppMainUI {

    private final POP3Client client;

    private final ListView<String> listView;
    private final MailView mailView;
    private final TextArea rawText;

    public AppMainUI(Stage stage, POP3Client client) {
        this.client = client;

        this.listView = new ListView<>();
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            show(newValue);
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(event -> {
            String id = listView.getSelectionModel().getSelectedItem();
            if (id != null) {
                Thread thread = new Thread(() -> {
                    try {
                        POP3ClientHelper.delete(client, id);
                        Platform.runLater(this::refreshListView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });
        contextMenu.getItems().add(remove);
        listView.setContextMenu(contextMenu);

        VBox box = new VBox(
                10,
                createLabel("Mails"),
                new HBox(
                        10,
                        reset("Revert modifications")
                ),
                listView
        );
        box.setPadding(new Insets(10));

        this.mailView = new MailView();
        Tab contentTab = new Tab("Content", mailView.getNode());
        contentTab.setClosable(false);

        this.rawText = new TextArea();
        rawText.setStyle("-fx-font-family: 'monospaced';");
        Tab rawTab = new Tab("Raw", rawText);
        rawTab.setClosable(false);

        TabPane tabPane = new TabPane(contentTab, rawTab);

        SplitPane splitPane = new SplitPane(box, tabPane);
        splitPane.setDividerPositions(0.35);

        stage.setTitle("POP3 client");
        Scene scene = new Scene(splitPane, 700, 500, true, SceneAntialiasing.BALANCED);
        stage.setScene(scene);

        refreshListView();
    }

    private void refreshListView() {
        listView.getItems().clear();
        Thread thread = new Thread(() -> {
            try {
                List<String> list = POP3ClientHelper.list(client);
                Platform.runLater(() -> {
                    listView.getItems().setAll(list);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void show(String id) {
        rawText.setText("");
        mailView.clean();

        if (id != null) {
            Thread thread = new Thread(() -> {
                try {
                    String rawMail = POP3ClientHelper.rawMail(client, id);
                    Platform.runLater(() -> {
                        rawText.setText(rawMail);
                    });

                    Session session = Session.getDefaultInstance(new Properties());
                    MimeMessage msg = new MimeMessage(session, new ByteArrayInputStream(rawMail.getBytes()));
                    MimeMessageParser parser = new MimeMessageParser(msg);
                    parser.parse();
                    Platform.runLater(() -> {
                        try {
                            mailView.show(parser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold");
        return label;
    }

    private Button reset(String label) {
        Button button = new Button(label);
        button.setOnAction(e -> {
            Thread thread = new Thread(() -> {
                try {
                    POP3ClientHelper.reset(client);
                    Platform.runLater(this::refreshListView);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            thread.setDaemon(true);
            thread.start();
        });
        return button;
    }

}