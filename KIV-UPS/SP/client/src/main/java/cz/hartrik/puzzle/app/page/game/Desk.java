package cz.hartrik.puzzle.app.page.game;

import java.util.List;

/**
 * Carries pieces and related information.
 *
 * @author Patrik Harag
 * @version 2017-10-29
 */
public class Desk {

    private final int w;
    private final int h;

    private final List<Piece> pieces;

    public Desk(int w, int h, List<Piece> pieces) {
        this.w = w;
        this.h = h;
        this.pieces = pieces;
    }

    /**
     * Returns width (pieces).
     *
     * @return width
     */
    public int getW() {
        return w;
    }

    /**
     * Returns height (pieces).
     *
     * @return height
     */
    public int getH() {
        return h;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Piece getNext(Piece p, Position position) {
        int index = p.getId();
        int x = index % getW();
        int y = (index - x) / getW();

        x += position.getDX();
        y += position.getDY();

        if (x < 0 || y < 0 || x >= getW() || y >= getH())
            return null;

        return pieces.get(x + y * getW());
    }
}
