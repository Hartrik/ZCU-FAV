package cz.hartrik.puzzle.app.page;

import javafx.scene.Node;

/**
 * Interface for one page in GUI.
 *
 * @author Patrik Harag
 * @version 2017-10-30
 */
public interface Page {

    /**
     * Returns JavaFX node of this page.
     *
     * @return node
     */
    Node getNode();

    default void onShow() {
        // nothing by default
    }

    default void onClose() {
        // nothing by default
    }

}
