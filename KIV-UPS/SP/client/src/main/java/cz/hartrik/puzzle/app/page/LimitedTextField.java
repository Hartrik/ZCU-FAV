package cz.hartrik.puzzle.app.page;

import javafx.scene.control.TextField;

/**
 * Text field that forbids from typing other than supported characters.
 *
 * @author Patrik Harag
 * @version 2017-10-31
 */
public class LimitedTextField extends TextField {

    private final String pattern;
    private final int maxLength;

    public LimitedTextField(String pattern, int maxLength) {
        this.pattern = pattern;
        this.maxLength = maxLength;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    private boolean validate(String text) {
        return text.matches(pattern)
                && (getText().length() + text.length()) <= maxLength;
    }
}