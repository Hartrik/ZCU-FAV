package cz.hartrik.puzzle.app.page.game;

/**
 * Marks relative position.
 *
 * @author Patrik Harag
 * @version 2017-10-29
 */
public enum  Position {

    TOP(0, -1), RIGHT(+1, 0), BOTTOM(0, +1), LEFT(-1, 0);

    private final int dx;
    private final int dy;

    Position(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDX() {
        return dx;
    }

    public int getDY() {
        return dy;
    }
}
