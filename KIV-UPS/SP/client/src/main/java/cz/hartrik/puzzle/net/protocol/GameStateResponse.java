package cz.hartrik.puzzle.net.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server response to the GST / GUP request.
 *
 * @author Patrik Harag
 * @version 2017-10-29
 */
public class GameStateResponse {

    /**
     * Represents a piece.
     */
    public static class Piece {
        int id;
        int x, y;

        public Piece(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        private Piece() {}

        public int getId() {
            return id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }


    private final List<Piece> pieces;
    private final Exception exception;

    private GameStateResponse(List<Piece> pieces) {
        this.pieces = pieces;
        this.exception = null;
    }

    private GameStateResponse(Exception e) {
        this.pieces = Collections.emptyList();
        this.exception = e;
    }

    /**
     * Returns true if the server response was corrupted.
     *
     * @return boolean
     */
    public boolean isCorrupted() {
        return exception != null;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public Exception getException() {
        return exception;
    }

    /**
     * Parses server response.
     *
     * @param string text
     * @return parsed response
     */
    public static GameStateResponse parse(String string) {
        try {
            List<Piece> list = new ArrayList<>();

            int i = 0;
            for (String pieceStr : string.split(";")) {
                String[] valStr = pieceStr.split(",");
                Piece p = new Piece();

                if (valStr.length == 2) {
                    p.id = i;
                    p.x = Integer.parseInt(valStr[0]);
                    p.y = Integer.parseInt(valStr[1]);

                } else if (valStr.length == 3) {
                    p.id = Integer.parseInt(valStr[0]);
                    p.x = Integer.parseInt(valStr[1]);
                    p.y = Integer.parseInt(valStr[2]);

                } else {
                    return new GameStateResponse(new RuntimeException("Wrong format!"));
                }

                list.add(p);
                i++;
            }

            return new GameStateResponse(list);

        } catch (Exception e) {
            return new GameStateResponse(e);
        }
    }

}
