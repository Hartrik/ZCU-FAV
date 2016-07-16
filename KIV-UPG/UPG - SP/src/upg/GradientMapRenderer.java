package upg;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Vykresluje mapu podle gradientu.
 *
 * @version 2016-05-06
 * @author Patrik Harag
 */
public class GradientMapRenderer extends MapRendererBase {

    private final Image gradient;
    private final int size;

    /**
     * Vytvoří novou instanci.
     *
     * @param gradient obrázek s gradientem (použije se první řádka)
     */
    public GradientMapRenderer(Image gradient) {
        this.gradient = gradient;
        this.size = (int) gradient.getWidth() - 1;
    }

    @Override
    public Color color(double value) {
        return gradient.getPixelReader().getColor((int) (value * size), 0);
    }

}