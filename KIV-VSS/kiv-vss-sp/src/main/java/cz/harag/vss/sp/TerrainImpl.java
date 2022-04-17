package cz.harag.vss.sp;

import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Implementace terénu jako 2D pole.
 *
 * @author Patrik Harag
 * @version 2019-12-27
 */
public class TerrainImpl implements Terrain {

    private final int width;
    private final int height;

    private final int[][] heightMap;
    private final IntSummaryStatistics elevationStats;

    private final double[][] waterMap;

    public TerrainImpl(int[][] heightMap) {
        this.width = heightMap.length;
        this.height = (heightMap.length > 0) ? heightMap[0].length : 0;
        for (int i = 0; i < width; i++) {
            if (heightMap[i].length != height) {
                throw new IllegalArgumentException("Height map must be rectangular");
            }
        }
        this.heightMap = heightMap;
        this.elevationStats = Stream.of(heightMap).flatMapToInt(IntStream::of).summaryStatistics();
        this.waterMap = new double[width][height];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getElevationAt(int x, int y) {
        return heightMap[x][y];
    }

    @Override
    public IntSummaryStatistics getElevationStatistics() {
        return elevationStats;
    }

    @Override
    public double getWaterAt(int x, int y) {
        return waterMap[x][y];
    }

    @Override
    public void setWaterAt(int x, int y, double level) {
        if (level < 0) {
            throw new IllegalArgumentException("Water level cannot be negative");
        }
        waterMap[x][y] = level;
    }

    @Override
    public void addWaterAt(int x, int y, double inc) {
        waterMap[x][y] = Math.max(0, waterMap[x][y] + inc);
    }

    @Override
    public String toString() {
        return "Terrain{" + "width=" + width + ", height=" + height + '}';
    }

}
