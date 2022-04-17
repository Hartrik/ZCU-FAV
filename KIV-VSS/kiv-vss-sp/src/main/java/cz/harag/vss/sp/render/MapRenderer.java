package cz.harag.vss.sp.render;

import javafx.scene.paint.Color;

/**
 * Rozhraní třídy, která renderuje výškovou mapu.
 *
 * @version 2019-12-23
 * @author Patrik Harag
 */
public interface MapRenderer {

    /**
     * Vrátí barvu podle hodnoty.
     *
     * @param value hodnota 0-1
     * @return barva
     */
    Color color(double value);

}