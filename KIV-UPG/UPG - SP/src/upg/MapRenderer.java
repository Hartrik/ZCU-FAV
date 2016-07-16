package upg;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Rozhraní třídy, která renderuje výškovou mapu.
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public interface MapRenderer {

    /**
     * Vykreslí mapu do obrázku.
     *
     * @param map mapa, která bude vykreslena
     * @return vykreslená mapa
     */
    WritableImage render(Map map);

    /**
     * Vrátí barvu podle hodnoty.
     *
     * @param value hodnota 0-1
     * @return barva
     */
    Color color(double value);

}