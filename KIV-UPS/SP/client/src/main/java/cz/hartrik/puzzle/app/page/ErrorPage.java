package cz.hartrik.puzzle.app.page;

import cz.hartrik.puzzle.app.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A page with a error message.
 *
 * @author Patrik Harag
 * @version 2017-10-10
 */
public class ErrorPage implements Page {

    private final Application application;
    private final Page nextPage;
    private final String message;

    public ErrorPage(Application application, Page nextPage, String message) {
        this.application = application;
        this.nextPage = nextPage;
        this.message = message;
    }

    @Override
    public Node getNode() {
        Text textTitle = new Text("An error occurred...");
        textTitle.setFont(Font.font(16));

        Label textMessage = new Label(message);
        textMessage.setFont(Font.font("monospaced", 12));
        textMessage.setStyle("-fx-text-fill: red;");
        textMessage.setWrapText(true);

        Button button = new Button("OK");
        button.setOnAction(event -> application.setActivePage(nextPage));
        button.setDefaultButton(true);
        button.setPrefWidth(100);
        button.requestFocus();

        VBox box = new VBox(textTitle, textMessage, button);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        return box;
    }

    public String getMessage() {
        return message;
    }
}
