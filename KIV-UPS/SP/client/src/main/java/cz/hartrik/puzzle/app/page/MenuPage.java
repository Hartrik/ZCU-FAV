package cz.hartrik.puzzle.app.page;

import cz.hartrik.common.Exceptions;
import cz.hartrik.puzzle.app.Application;
import cz.hartrik.puzzle.net.ConnectionHolder;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A page with a main menu.
 *
 * @author Patrik Harag
 * @version 2018-01-23
 */
public class MenuPage extends PageBase {

    private final Page previousPage;

    public MenuPage(Application app, Page previousPage) {
        super(app);
        this.previousPage = previousPage;
    }

    @Override
    public Node getNode() {
        Text title = new Text("Online Puzzle");
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 32));

        VBox dummy = new VBox();
        dummy.setMinHeight(50);

        Button bNew = new Button("New game");
        bNew.setOnAction(event -> onNewGame());
        bNew.setPrefWidth(100);

        Button bJoin = new Button("Join game");
        bJoin.setOnAction(event -> onJoinGame());
        bJoin.setPrefWidth(100);

        Button bLogOut = new Button("Log out");
        bLogOut.setOnAction(event -> onLogOut());
        bLogOut.setPrefWidth(100);

        VBox box = new VBox(title, dummy, bNew, bJoin, bLogOut);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        return box;
    }

    private void onNewGame() {
        Page page = new NewGamePage(application, this);
        application.setActivePage(page);
    }

    private void onJoinGame() {
        Page page = new JoinGamePage(application, this);
        application.setActivePage(page);
    }

    private void onLogOut() {
        application.setActivePage(new LoadingPage());
        application.getConnection().async(
            c -> {
                c.sendLogOut().get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                logOut();
            },
            e -> {
                /* errors ignored... */
                logOut();
            }
        );
    }

    private void logOut() {
        application.setActivePage(previousPage);
        Exceptions.silent(() -> {
            ConnectionHolder connection = application.getConnection();
            if (connection != null)
                connection.close();
        });
    }

}
