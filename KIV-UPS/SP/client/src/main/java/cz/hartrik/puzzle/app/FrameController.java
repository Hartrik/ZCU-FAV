package cz.hartrik.puzzle.app;

import cz.hartrik.puzzle.app.page.Page;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX controller for main window.
 *
 * @version 2017-10-30
 * @author Patrik Harag
 */
public class FrameController implements Initializable {

    @FXML protected StackPane rootPane;

    private Page activePage;

    public void setActivePage(Page activePage) {
        if (this.activePage != null) {
            this.activePage.onClose();
            getRootPane().getChildren().remove(0);
        }

        this.activePage = activePage;
        getRootPane().getChildren().add(0, activePage.getNode());
        activePage.onShow();
    }

    public StackPane getRootPane() {
        return rootPane;
    }

    public Page getActivePage() {
        return activePage;
    }

    // Initializable

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // ostatní

    public Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

}