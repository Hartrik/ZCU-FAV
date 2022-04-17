package cz.hartrik.puzzle.app.page.game;

import java.util.IntSummaryStatistics;
import java.util.Set;

/**
 * Finalize move of a piece in the target destination.
 *
 * @author Patrik Harag
 * @version 2017-10-29
 */
public class PieceMoveFinalizer {

    static final int MAX_DIFF = Piece.SIZE * 5;  // pt
    static final int MAX_PIECE_SNAP_DISTANCE = Piece.SIZE / 5;  // pt

    private final Desk desk;
    private final PieceGroupManager groupManager;

    public PieceMoveFinalizer(Desk desk) {
        this.desk = desk;
        this.groupManager = new PieceGroupManager(desk);
    }

    public PieceGroupManager getGroupManager() {
        return groupManager;
    }

    /**
     * Correction after move.
     *
     * @param piece moved piece
     */
    public void move(Piece piece) {
        int x = piece.getX();
        int y = piece.getY();

        // limit desk
        IntSummaryStatistics xStats = desk.getPieces().stream().filter(p -> p != piece)
                .mapToInt(Piece::getX).summaryStatistics();
        IntSummaryStatistics yStats = desk.getPieces().stream().filter(p -> p != piece)
                .mapToInt(Piece::getY).summaryStatistics();

        x = Math.min(x, xStats.getMax() + MAX_DIFF);
        x = Math.max(x, xStats.getMin() - MAX_DIFF);

        y = Math.min(y, yStats.getMax() + MAX_DIFF);
        y = Math.max(y, yStats.getMin() - MAX_DIFF);

        // move piece
        if (x != piece.getX() || y != piece.getY()) {
            piece.moveX(x);
            piece.moveY(y);
        }
    }

    /**
     * Correction after move complete / after drag and drop.
     *
     * @param piece moved piece
     * @return group
     */
    public Set<Piece> moveComplete(Piece piece) {
        // snap to a near piece (piece group)

        Piece nearestPieceOrigin = null;
        double nearestDistance = Double.MAX_VALUE;
        Piece nearestPiece = null;
        Position nearestPosition = null;

        Set<Piece> group = groupManager.getAllConnected(piece);
        for (Piece p : group) {

            for (Position position : Position.values()) {
                Piece next = desk.getNext(p, position);
                if (next == null) continue;

                double distance = groupManager.distance(p, next, position);
                if (distance < nearestDistance
                        // not in the same group
                        && distance > PieceGroupManager.MAX_PIECE_GROUP_DISTANCE) {

                    nearestPieceOrigin = p;
                    nearestPosition = position;
                    nearestDistance = distance;
                    nearestPiece = next;
                }
            }
        }

        if (nearestDistance <= MAX_PIECE_SNAP_DISTANCE) {
            int oldX = nearestPieceOrigin.getX();
            int oldY = nearestPieceOrigin.getY();
            int newX = nearestPiece.getX() + (-1 * nearestPosition.getDX() * Piece.SIZE);
            int newY = nearestPiece.getY() + (-1 * nearestPosition.getDY() * Piece.SIZE);

            int diffX = newX - oldX;
            int diffY = newY - oldY;

            for (Piece p : group) {
                p.moveX(p.getX() + diffX);
                p.moveY(p.getY() + diffY);
            }
        }
        return group;
    }

}
