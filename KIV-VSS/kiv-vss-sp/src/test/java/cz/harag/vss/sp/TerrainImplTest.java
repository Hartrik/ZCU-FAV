package cz.harag.vss.sp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Patrik Harag
 * @version 2019-12-27
 */
public class TerrainImplTest {

    @Test
    public void testConstructor() {
        Terrain terrain = new TerrainImpl(new int[][] {
                {100, 80, 70},
                {80, 70, 50},
                {70, 50, 20},
                {50, 20, 0}
        });
        assertEquals(80, terrain.getElevationAt(0, 1));
        assertEquals(4, terrain.getWidth());
        assertEquals(3, terrain.getHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNotRectangular() {
        Terrain terrain = new TerrainImpl(new int[][] {
                {100, 80, 70},
                {50, 20}
        });
    }

}