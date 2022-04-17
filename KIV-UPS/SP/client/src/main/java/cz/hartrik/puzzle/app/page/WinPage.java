package cz.hartrik.puzzle.app.page;

import cz.hartrik.puzzle.app.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * A page with a win message.
 *
 * @author Patrik Harag
 * @version 2017-10-29
 */
public class WinPage extends CancelablePage {

    private final Image image;

    public WinPage(Application application, Page nextPage, Image image) {
        super(application, "WIN", nextPage);
        this.image = image;
    }

    @Override
    public Node getContent() {
        ImageView view = new ImageView(image);

        VBox box = new VBox(view);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        return box;
    }

}
