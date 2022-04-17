package cz.harag.vss.sp.render;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Vykresluje mapu podle gradientu.
 *
 * @version 2019-11-21
 * @author Patrik Harag
 */
public class GradientMapRenderer implements MapRenderer {

    private final Image gradient;
    private final int size;
    private final boolean reverted;

    /**
     * Vytvoří novou instanci.
     *
     * @param gradient obrázek s gradientem (použije se první řádka)
     */
    public GradientMapRenderer(Image gradient, boolean reverted) {
        this.gradient = gradient;
        this.size = (int) gradient.getWidth() - 1;
        this.reverted = reverted;
    }

    @Override
    public Color color(double value) {
        int c = (int) (value * size);
        if (reverted) {
            c = size - c;
        }
        return gradient.getPixelReader().getColor(c, 0);
    }

}