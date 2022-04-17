package cz.hartrik.puzzle.app.page;

import cz.hartrik.puzzle.app.Application;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * A page with a cancel button.
 *
 * @author Patrik Harag
 * @version 2017-10-30
 */
public abstract class CancelablePage extends PageBase {

    private final String title;
    private final Page previousPage;

    public CancelablePage(Application app, String title, Page previousPage) {
        super(app);
        this.title = title;
        this.previousPage = previousPage;
    }

    @Override
    public final Node getNode() {
        Text tTitle = new Text(title);
        tTitle.setFont(Font.font("Tahoma", 24));

        Button bLogOut = new Button("Cancel");
        bLogOut.setOnAction(event -> {
            application.setActivePage(previousPage);
        });
        bLogOut.setPrefWidth(100);

        Pane dummy1 = new Pane();
        HBox.setHgrow(dummy1, Priority.SOMETIMES);

        Pane dummy2 = new Pane();
        HBox.setHgrow(dummy2, Priority.SOMETIMES);

        ToolBar toolBar = new ToolBar(dummy1, tTitle, dummy2, bLogOut);

        VBox box = new VBox(toolBar, getContent());
        box.setSpacing(20);
        return box;
    }

    protected Page getPreviousPage() {
        return previousPage;
    }

    protected abstract Node getContent();

}
