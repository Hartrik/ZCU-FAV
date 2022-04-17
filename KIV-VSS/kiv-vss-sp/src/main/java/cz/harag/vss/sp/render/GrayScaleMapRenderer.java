package cz.harag.vss.sp.render;

import javafx.scene.paint.Color;


/**
 * Třída vykresluje mapu ve stupních šedi.
 * Čím světlejší bod, tím výš.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public class GrayScaleMapRenderer implements MapRenderer {

    @Override
    public Color color(double value) {
        return Color.gray(value);
    }

}