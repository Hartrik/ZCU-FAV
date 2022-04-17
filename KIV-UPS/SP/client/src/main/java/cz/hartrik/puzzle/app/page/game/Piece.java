package cz.hartrik.puzzle.app.page.game;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Represents a puzzle piece
 *
 * @author Patrik Harag
 * @version 2017-10-30
 */
public class Piece {

    public static final int SIZE = 100;

    private final int id;
    private final DoubleProperty xPos = new SimpleDoubleProperty();
    private final DoubleProperty yPos = new SimpleDoubleProperty();

    private Integer lastSyncX;
    private Integer lastSyncY;

    private PieceNode node;

    private Runnable onPieceMoveComplete;

    private SimpleBooleanProperty synced = new SimpleBooleanProperty(false);

    /**
     * Creates a new piece.
     *
     * @param id piece id
     * @param image image
     * @param xInImage horizontal position in the image
     * @param yInImage vertical position in the image
     */
    public Piece(int id, Image image, final double xInImage, final double yInImage,
                 PieceMoveFinalizer moveFinalizer) {

        this.id = id;
        this.node = new PieceNode(this, image, xInImage, yInImage, moveFinalizer);

        xPos.bind(node.translateXProperty().add(xInImage));
        yPos.bind(node.translateYProperty().add(yInImage));

        // init synced property
        xPos.addListener((ov, o, n) -> {
            testSync();
        });
        yPos.addListener((ov, o, n) -> {
            testSync();
        });

        node.setOnMouseReleased(event -> {
            if (onPieceMoveComplete != null)
                onPieceMoveComplete.run();
        });

        synced.addListener((ov, o, n) -> {
            Platform.runLater(() -> {
                if (o == n) return;
                if (n) {
                    node.setEffect(null);
                } else {
                    DropShadow effect = new DropShadow();
                    effect.setColor(Color.RED);
                    node.setEffect(effect);
                }
            });
        });
    }

    private void testSync() {
        boolean syncX = lastSyncX != null && lastSyncX.equals(getX());
        boolean syncY = lastSyncY != null && lastSyncY.equals(getY());

        synced.set(syncX && syncY);
    }

    public void setOnPieceMoveComplete(Runnable onPieceMoveComplete) {
        this.onPieceMoveComplete = onPieceMoveComplete;
    }

    public PieceNode getNode() {
        return node;
    }

    public int getSize() {
        return SIZE;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return (int) xPos.get();
    }

    public int getY() {
        return (int) yPos.get();
    }

    public void moveX(double x) {
        node.translateXProperty().set(x - node.getXInImage());
    }

    public void moveY(double y) {
        node.translateYProperty().set(y - node.getYInImage());
    }

    public void setLastSyncX(int lastSyncX) {
        this.lastSyncX = lastSyncX;
        testSync();
    }

    public void setLastSyncY(int lastSyncY) {
        this.lastSyncY = lastSyncY;
        testSync();
    }

    public Integer getLastSyncX() {
        return lastSyncX;
    }

    public Integer getLastSyncY() {
        return lastSyncY;
    }

    public SimpleBooleanProperty syncedProperty() {
        return synced;
    }

    public boolean isSynced() {
        return synced.get();
    }

    public boolean changed() {
        return !synced.get();
    }
}