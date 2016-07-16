package upg;

import java.util.IntSummaryStatistics;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 *
 * @version 2016-05-04
 * @author Patrik Harag
 */
public abstract class MapRendererBase implements MapRenderer {

    /**
     * Vykreslí mapu do obrázku.
     * Čím světlejší bod, tím výš. Pokud bude na celé mapě rovina, bude použita
     * střední barva.
     *
     * @param map mapa, která bude vykreslena
     * @return vykreslená mapa
     */
    @Override
    public WritableImage render(Map map) {
        IntSummaryStatistics stats = coutStatistics(map);

        WritableImage img = new WritableImage(map.getWidth(), map.getHeight());
        PixelWriter pw = img.getPixelWriter();

        IntFunction<Color> colorFunc = (stats.getMin() == stats.getMax())
                ? (h) -> color(.5)
                : (h) -> color(relativize(h, stats));

        write(pw, map, colorFunc);

        return img;
    }

    private void write(PixelWriter pw, Map map, IntFunction<Color> function) {
        for (int y = 0; y < map.getHeight(); y++)
            for (int x = 0; x < map.getWidth(); x++)
                pw.setColor(x, y, function.apply(map.heightMap[y][x]));
    }

    public static double relativize(int h, IntSummaryStatistics stats) {
        return ((double) h - stats.getMin()) / (stats.getMax() - stats.getMin());
    }

    public static IntSummaryStatistics coutStatistics(Map map) {
        return Stream.of(map.getHeightMap())
                .flatMapToInt(IntStream::of)
                .summaryStatistics();
    }

}