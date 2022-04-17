package cz.harag.vss.sp.render;

import cz.harag.vss.sp.Terrain;

import java.util.IntSummaryStatistics;
import java.util.function.IntFunction;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * @author Patrik Harag
 * @version 2019-12-23
 */
public class MapRendererUtils {

    /**
     * Vykreslí mapu do obrázku.
     * Čím světlejší bod, tím výš. Pokud bude na celé mapě rovina, bude použita
     * střední barva.
     *
     * @param terrain mapa, která bude vykreslena
     * @return vykreslená mapa
     */
    public static final WritableImage render(Terrain terrain, MapRenderer renderer) {
        WritableImage img = new WritableImage(terrain.getWidth(), terrain.getHeight());
        PixelWriter pw = img.getPixelWriter();

        IntFunction<Color> colorFunc = createElevationToColorFunc(terrain, renderer);
        write(pw, terrain, colorFunc);

        return img;
    }

    private static void write(PixelWriter pw, Terrain terrain, IntFunction<Color> function) {
        for (int y = 0; y < terrain.getHeight(); y++) {
            for (int x = 0; x < terrain.getWidth(); x++) {
                Color color = function.apply(terrain.getElevationAt(x, y));
                pw.setColor(x, y, color);
            }
        }
    }

    public static IntFunction<Color> createElevationToColorFunc(Terrain terrain, MapRenderer renderer) {
        IntSummaryStatistics stats = terrain.getElevationStatistics();
        return (stats.getMin() == stats.getMax())
                ? (h) -> renderer.color(.5)
                : (h) -> renderer.color(relativize(h, stats));
    }

    public static double relativize(int h, IntSummaryStatistics stats) {
        return ((double) h - stats.getMin()) / (stats.getMax() - stats.getMin());
    }


    private MapRendererUtils() {
        // hidden
    }
}
