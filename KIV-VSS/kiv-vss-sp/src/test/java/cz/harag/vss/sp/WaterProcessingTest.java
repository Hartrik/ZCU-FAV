package cz.harag.vss.sp;

import java.util.Random;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Patrik Harag
 * @version 2019-12-27
 */
public class WaterProcessingTest {

    private final Terrain terrain = new TerrainImpl(new int[][]{
            {100, 80, 70},
            {80, 70, 50},
            {70, 50, 20}
    });
    private final Random random = new Random(42);
    private final WaterProcessing waterProcessing = new WaterProcessing(terrain, random);

    @Test
    public void testSimulate1() {
        terrain.setWaterAt(0, 0, 10);
        for (int i = 0; i < 1000; i++) {
            waterProcessing.simulate();
        }
        assertTrue(terrain.getWaterAt(0, 0) < WaterProcessing.WATER_LEVEL_DELTA);
        assertTrue(terrain.getWaterAt(2, 2) > 9.5);
    }

    @Test
    public void testSimulate2() {
        waterProcessing.addWater(1);
        for (int i = 0; i < 1000; i++) {
            waterProcessing.simulate();
        }
        assertTrue(terrain.getWaterAt(0, 0) < WaterProcessing.WATER_LEVEL_DELTA);
        assertTrue(terrain.getWaterAt(2, 2) > 8);
    }

}