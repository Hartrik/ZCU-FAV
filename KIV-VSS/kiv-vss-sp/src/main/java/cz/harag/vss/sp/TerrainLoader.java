package cz.harag.vss.sp;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * Nástroj pro načítání terénů.
 *
 * @author Patrik Harag
 * @version 2019-11-21
 */
public class TerrainLoader {

    /**
     * Načte terén z obrázku. Použije pouze červenou složku.
     *
     * @param image obrázek
     * @param maxElevation maximální výška, tam kde R=255
     * @return terén
     */
    public static Terrain fromImage(Image image, int maxElevation) {
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        int[][] heightMap = new int[width][height];

        PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                heightMap[x][y] = (int) (color.getRed() * maxElevation);
            }
        }
        return new TerrainImpl(heightMap);
    }

}
