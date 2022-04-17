package cz.hartrik.puzzle.app.page;

import cz.hartrik.puzzle.app.Application;
import cz.hartrik.puzzle.app.page.game.ImageManager;
import cz.hartrik.puzzle.net.protocol.GameListResponse;
import cz.hartrik.puzzle.net.protocol.GameStateResponse;
import cz.hartrik.puzzle.net.protocol.JoinGameResponse;
import cz.hartrik.puzzle.app.page.game.PuzzlePage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * A page with a game selection.
 *
 * @author Patrik Harag
 * @version 2017-10-30
 */
public class JoinGamePage extends CancelablePage {

    private static final int PREVIEW_W = 200;
    private static final int PREVIEW_H = 150;

    private static final String TITLE = "Join Game";

    private final FlowPane box;

    public JoinGamePage(Application app, Page previousPage) {
        super(app, TITLE, previousPage);
        this.box = new FlowPane();
    }

    @Override
    public Node getContent() {
        box.setVgap(10);
        box.setHgap(10);
        box.setPrefWrapLength(450);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    @Override
    public void onShow() {
        box.getChildren().setAll(new ProgressIndicator());

        application.getConnection().async(
            c -> {
                Future<GameListResponse> listF = c.sendGameList();
                GameListResponse listR = listF.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

                Platform.runLater(() -> onUpdate(listR));
            },
            e -> {
                application.logException("On login:", e);
                Platform.runLater(() -> onError(e));
            }
        );
    }

    private void onError(Exception e) {
        Label textMessage = new Label(e.toString());
        textMessage.setFont(Font.font("monospaced", 12));
        textMessage.setStyle("-fx-text-fill: red;");
        textMessage.setWrapText(true);

        Button button = new Button("Retry");
        button.setOnAction(event -> {
            onShow();
        });

        VBox errorBox = new VBox();
        errorBox.getChildren().setAll(textMessage, button);
        errorBox.setSpacing(20);
        box.getChildren().setAll(textMessage, errorBox);
    }

    private void onUpdate(GameListResponse list) {
        box.getChildren().clear();

        if (list.isCorrupted()) {
            onError(list.getException());

        } else {
            for (GameListResponse.Game game : list.getGames()) {
                int id = game.getGameID();
                int w = game.getWidth();
                int h = game.getHeight();

                Image image = ImageManager.getInstance().getImageBySize(w, h);

                ImageView view = new ImageView(image);
                view.setFitWidth(PREVIEW_W - 10);
                view.setFitHeight(PREVIEW_H - 30);
                view.setPreserveRatio(true);

                String desc = String.format("id=%d, size=%dx%d", id, w, h);
                Button button = new Button(desc, view);
                button.setMinSize(PREVIEW_W, PREVIEW_H);
                button.setMaxSize(PREVIEW_W, PREVIEW_H);
                button.setContentDisplay(ContentDisplay.TOP);
                button.setOnAction(event -> {
                    onJoinGame(image, id);
                });

                box.getChildren().add(button);
            }
        }
    }

    private void onJoinGame(Image image, int gameID) {
        application.setActivePage(new LoadingPage());
        application.getConnection().async(
            c -> {
                Future<JoinGameResponse> joinF = c.sendJoinGame(gameID);
                JoinGameResponse joinR = joinF.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (joinR != JoinGameResponse.OK) {
                    application.log("Join status: " + joinR);
                    Page page = new ErrorPage(application, this, joinR.name());
                    application.setActivePage(page);
                    return;
                }

                Future<GameStateResponse> stateF = c.sendGameStateUpdate(gameID);
                GameStateResponse stateR = stateF.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (stateR.isCorrupted()) {
                    application.logException("Game status:", stateR.getException());
                    Page page = new ErrorPage(application, this, stateR.getException().toString());
                    application.setActivePage(page);
                    return;
                }

                Page page = new PuzzlePage(application, gameID, stateR, image, getPreviousPage());
                application.setActivePage(page);
            },
            e -> {
                application.logException("Join exception:", e);
                Page page = new ErrorPage(application, this, e.toString());
                application.setActivePage(page);
            }
        );
    }


}
