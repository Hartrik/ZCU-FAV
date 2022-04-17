package cz.hartrik.puzzle.app.page;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 * A page with a progress indicator.
 *
 * @author Patrik Harag
 * @version 2017-10-10
 */
public class LoadingPage implements Page {

    @Override
    public Node getNode() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setPrefSize(50, 50);

        VBox box = new VBox(indicator);
        box.setAlignment(Pos.CENTER);
        return box;
    }

}
