package cz.hartrik.puzzle.app.page.game;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Patrik Harag
 * @version 2018-01-23
 */
class CheatManager {

    private static final Logger LOGGER = Logger.getLogger(CheatManager.class.getName());

    private final PuzzlePage puzzlePage;
    private StringBuilder buffer;

    CheatManager(PuzzlePage puzzlePage) {
        this.puzzlePage = puzzlePage;
        this.buffer = new StringBuilder(maxSequenceLength());
    }

    void register() {
        puzzlePage.getNode().setOnKeyPressed(e -> {
            String c = e.getText();
            if (c.length() != 1)
                return;

            append(c);

            String sequence = buffer.toString().toLowerCase();
            for (Cheat cheat : Cheat.values()) {
                if (sequence.endsWith(cheat.getSequence().toLowerCase())) {
                    LOGGER.info("Cheat triggered: " + cheat);
                    cheat.run(puzzlePage);
                }
            }
        });
    }

    private void append(String c) {
        if (buffer.length() == buffer.capacity()) {
            // full buffer
            buffer.deleteCharAt(0);
        }

        buffer.append(c);
    }

    private int maxSequenceLength() {
        return Stream.of(Cheat.values())
                .map(Cheat::getSequence).mapToInt(String::length).max()
                .orElseThrow(AssertionError::new);
    }

}
