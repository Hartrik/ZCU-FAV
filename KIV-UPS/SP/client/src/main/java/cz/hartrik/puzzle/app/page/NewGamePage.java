package cz.hartrik.puzzle.app.page;

import cz.hartrik.puzzle.app.Application;
import cz.hartrik.puzzle.app.page.game.ImageManager;
import cz.hartrik.puzzle.app.page.game.Piece;
import cz.hartrik.puzzle.net.protocol.GameStateResponse;
import cz.hartrik.puzzle.net.protocol.JoinGameResponse;
import cz.hartrik.puzzle.net.protocol.NewGameResponse;
import cz.hartrik.puzzle.app.page.game.PuzzlePage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

/**
 * A page with a new game selection.
 *
 * @author Patrik Harag
 * @version 2017-10-17
 */
public class NewGamePage extends CancelablePage {

    private static final int PREVIEW_W = 200;
    private static final int PREVIEW_H = 150;

    private static final String TITLE = "New Game";

    public NewGamePage(Application app, Page previousPage) {
        super(app, TITLE, previousPage);
    }

    @Override
    public Node getContent() {
        FlowPane box = new FlowPane();
        box.setVgap(10);
        box.setHgap(10);
        box.setPrefWrapLength(450);
        box.setAlignment(Pos.CENTER);

        for (Image image : ImageManager.getInstance().getImages()) {
            int w = (int) (image.getWidth() / Piece.SIZE);
            int h = (int) (image.getHeight() / Piece.SIZE);

            ImageView view = new ImageView(image);
            view.setFitWidth(PREVIEW_W - 10);
            view.setFitHeight(PREVIEW_H - 30);
            view.setPreserveRatio(true);

            Button button = new Button(String.format("%d x %d", w, h), view);
            button.setMinSize(PREVIEW_W, PREVIEW_H);
            button.setMaxSize(PREVIEW_W, PREVIEW_H);
            button.setContentDisplay(ContentDisplay.TOP);
            button.setOnAction(event -> {
                onNewGame(w, h, image);
            });

            box.getChildren().add(button);
        }

        return box;
    }

    private void onNewGame(int w, int h, Image image) {
        application.setActivePage(new LoadingPage());
        application.getConnection().async(
            c -> {
                Future<NewGameResponse> newF = c.sendNewGame(w, h);
                NewGameResponse newR = newF.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (newR.getStatus() != NewGameResponse.Status.OK) {
                    application.log("New game status: " + newR.getStatus());
                    Page page = new ErrorPage(application, this, newR.getStatus().name());
                    application.setActivePage(page);
                    return;
                }

                Future<JoinGameResponse> joinF = c.sendJoinGame(newR.getGameID());
                JoinGameResponse joinR = joinF.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (joinR != JoinGameResponse.OK) {
                    application.log("Join status: " + joinR);
                    Page page = new ErrorPage(application, this, joinR.name());
                    application.setActivePage(page);
                    return;
                }

                Future<GameStateResponse> stateF = c.sendGameStateUpdate(newR.getGameID());
                GameStateResponse stateR = stateF.get(Application.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (stateR.isCorrupted()) {
                    application.logException("Game status:", stateR.getException());
                    Page page = new ErrorPage(application, this, stateR.getException().toString());
                    application.setActivePage(page);
                    return;
                }

                Page gamePage = new PuzzlePage(application, newR.getGameID(), stateR, image, getPreviousPage());
                application.setActivePage(gamePage);
            },
            e -> {
                application.logException("New game exception:", e);
                Page page = new ErrorPage(application, this, e.toString());
                application.setActivePage(page);
            }
        );
    }

}
