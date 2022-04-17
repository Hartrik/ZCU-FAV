package cz.hartrik.puzzle.app.page.game;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Manages piece groups.
 *
 * @author Patrik Harag
 * @version 2017-10-29
 */
public class PieceGroupManager {

    static final double MAX_PIECE_GROUP_DISTANCE = 0.5;  // pt

    private final Desk desk;

    public PieceGroupManager(Desk desk) {
        this.desk = desk;
    }

    /**
     * Collect pieces that are connected with given piece.
     *
     * @param piece piece
     * @return group
     */
    public Set<Piece> getAllConnected(Piece piece) {
        LinkedHashSet<Piece> group = new LinkedHashSet<>();
        group.add(piece);
        findAllConnected(piece, group);
        return group;
    }

    private void findAllConnected(Piece piece, Set<Piece> group) {
        for (Position position : Position.values()) {
            Piece next = desk.getNext(piece, position);
            if (next == null || group.contains(next)) continue;

            double distance = distance(piece, next, position);
            if (distance < MAX_PIECE_GROUP_DISTANCE) {
                group.add(next);

                findAllConnected(next, group);
            }
        }
    }

    /**
     * Returns distance to the expected location.
     *
     * @param piece this piece
     * @param other other piece
     * @param position position of other piece (from this piece)
     * @return distance
     */
    public double distance(Piece piece, Piece other, Position position) {
        int dx = piece.getX() - (other.getX() + (-1 * position.getDX() * Piece.SIZE));
        int dy = piece.getY() - (other.getY() + (-1 * position.getDY() * Piece.SIZE));
        return Math.sqrt((dx * dx) + (dy * dy));
    }

}
