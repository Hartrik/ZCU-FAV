package cz.harag.vss.sp;

import java.util.Random;

/**
 * Nástroj pro práci s vodou.
 *
 * @author Patrik Harag
 * @version 2020-01-09
 */
public class WaterProcessing {

    /**
     * Hodnota pod kterou už se voda ignoruje.
     */
    public static final double WATER_LEVEL_DELTA = 0.1;

    /**
     * Poměr v jakém se voda vyrovnává.
     */
    public static final double DEFAULT_BALANCE_RATIO = 0.5;

    private static final int RANDOM_DATA_COUNT = 100;

    private final Terrain terrain;
    private final Random random;

    private double balanceRatio = DEFAULT_BALANCE_RATIO;

    // indexy pro náhodné procházení
    private final int[][] horIterationOrder;
    private final int[][] verIterationOrder;

    public WaterProcessing(Terrain terrain, Random random) {
        this.terrain = terrain;
        this.random = random;
        this.horIterationOrder = generateRandomData(RANDOM_DATA_COUNT, terrain.getWidth(), random);
        this.verIterationOrder = generateRandomData(RANDOM_DATA_COUNT, terrain.getHeight(), random);
    }

    private int[][] generateRandomData(int count, int size, Random random) {
        int[][] data = new int[count][size];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < size; j++) {
                data[i][j] = random.nextInt(size);
            }
        }
        return data;
    }

    public void setBalanceRatio(double balanceRatio) {
        this.balanceRatio = balanceRatio;
    }

    /**
     * Provede jeden krok simulace.
     */
    public void simulate() {
        int[] verIndexes = verIterationOrder[random.nextInt(RANDOM_DATA_COUNT)];
        int[] horIndexes = horIterationOrder[random.nextInt(RANDOM_DATA_COUNT)];
        for (int x : horIndexes) {
            for (int y : verIndexes) {
                process(x, y);
            }
        }
    }

    private void process(int x, int y) {
        double waterHeight = terrain.getWaterAt(x, y);
        if (waterHeight < WATER_LEVEL_DELTA) {
            // not enough water - skip
            return;
        }
        int terrainHeight = terrain.getElevationAt(x, y);
        double totalHeight = terrainHeight + waterHeight;

        // find min
        double minTotalHeight = Double.MAX_VALUE;
        Direction minDirection = null;
        for (Direction direction : Direction.values()) {
            int ix = x + direction.getX();
            int iy = y + direction.getY();
            if (ix < 0 || iy < 0 || ix >= terrain.getWidth() || iy >= terrain.getHeight()) {
                // out of bounds
                continue;
            }
            int th = terrain.getElevationAt(ix, iy);
            double wh = terrain.getWaterAt(ix, iy);
            double h = th + wh;
            if (h < totalHeight && (minDirection == null || h < minTotalHeight)) {
                minTotalHeight = h;
                minDirection = direction;
            }
        }

        // balance
        if (minDirection != null) {
            double change = Math.min(waterHeight * balanceRatio, (totalHeight - minTotalHeight) * balanceRatio);
            terrain.addWaterAt(x, y, -change);
            terrain.addWaterAt(x + minDirection.getX(), y + minDirection.getY(), change);
        }
    }

    /**
     * Přidá určitou úroveň vody všem políčkům v terénu.
     *
     * @param level úroveň vody
     */
    public void addWater(int level) {
        for (int i = 0; i < terrain.getWidth(); i++) {
            for (int j = 0; j < terrain.getHeight(); j++) {
                terrain.addWaterAt(i, j, level);
            }
        }
    }

    /**
     * Nastaví vodu na danou úroveň.
     *
     * @param level úroveň vody
     */
    public void setWaterTo(int level) {
        for (int i = 0; i < terrain.getWidth(); i++) {
            for (int j = 0; j < terrain.getHeight(); j++) {
                terrain.setWaterAt(i, j, level);
            }
        }
    }
}
