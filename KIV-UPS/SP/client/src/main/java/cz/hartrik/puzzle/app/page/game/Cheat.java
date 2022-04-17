package cz.hartrik.puzzle.app.page.game;

/**
 * Cheat definitions.
 *
 * @author Patrik Harag
 * @version 2018-01-23
 */
enum Cheat {

    SOLVE("hesoyam") {

        @Override
        void run(PuzzlePage page) {
            for (Piece piece : page.getPieces()) {
                PieceNode node = piece.getNode();
                piece.moveX(node.getXInImage());
                piece.moveY(node.getYInImage());
            }
        }
    }

    ;

    private final String sequence;

    Cheat(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return sequence;
    }

    abstract void run(PuzzlePage page);

}
