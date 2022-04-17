package cz.harag.vss.sp.render;

import javafx.scene.image.Image;

/**
 * Třída sdružující renderery.
 *
 * @author Patrik Harag
 * @version 2019-12-23
 */
public final class MapRenderers {

    public static final MapRenderer DESERT_1 = new GradientMapRenderer(
            new Image("/cz/harag/vss/sp/gradient - desert 1.png"), false);
    public static final MapRenderer DESERT_2 = new GradientMapRenderer(
            new Image("/cz/harag/vss/sp/gradient - desert 2.png"), false);
    public static final MapRenderer FOREST = new GradientMapRenderer(
            new Image("/cz/harag/vss/sp/gradient - forests.png"), true);
    public static final MapRenderer GRAYSCALE = new GrayScaleMapRenderer();

    private MapRenderers() {
        // hidden
    }
}
