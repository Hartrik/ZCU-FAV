package cz.harag.vss.sp.ui;

import java.util.function.IntFunction;

import cz.harag.vss.sp.render.MapRenderer;
import cz.harag.vss.sp.Terrain;
import cz.harag.vss.sp.WaterProcessing;
import cz.harag.vss.sp.render.MapRendererUtils;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * Obstarává 3D objekty terénu.
 *
 * @author Patrik Harag
 * @version 2019-12-23
 */
public class Terrain3D {

    private static final double SCALE = 0.1;
    private static final double GAP = 0.05;
    private static final double MIN_VISIBLE_WATER_LEVEL = WaterProcessing.WATER_LEVEL_DELTA;

    private static final Color WATER_COLOR = new Color(20/255., 65/255., 105/255., 0.7);

    private final Terrain terrain;
    private final MapRenderer renderer;

    private Box[][] terrainBlocks;
    private Box[][] waterBlocks;
    private Box pedestal;

    public Terrain3D(Terrain terrain, MapRenderer renderer) {
        this.terrain = terrain;
        this.renderer = renderer;
        buildMap();
    }

    private void buildMap() {
        double blockSize = 1;
        double centerX = blockSize / 2. - terrain.getWidth() / 2.;
        double centerY = blockSize / 2. - terrain.getHeight() / 2.;

        IntFunction<Color> colorFunc = MapRendererUtils.createElevationToColorFunc(terrain, renderer);

        // podstavec
        this.pedestal = new Box(terrain.getWidth(), terrain.getHeight(), 10);
        pedestal.setMaterial(new PhongMaterial(Color.GRAY));
        pedestal.setTranslateZ(5);

        // terén
        this.terrainBlocks = new Box[terrain.getWidth()][terrain.getHeight()];
        this.waterBlocks = new Box[terrain.getWidth()][terrain.getHeight()];
        for (int x = 0; x < terrain.getWidth(); x++) {
            for (int y = 0; y < terrain.getHeight(); y++) {
                int h = terrain.getElevationAt(x, y);
                double rh = h * SCALE;

                Box terrainBlock = new Box(blockSize, blockSize, rh);
                terrainBlock.setMaterial(new PhongMaterial(colorFunc.apply(h)));
                terrainBlock.setTranslateX(centerX + x * blockSize);
                terrainBlock.setTranslateY(centerY + y * blockSize);
                terrainBlock.setTranslateZ(- rh / 2 - GAP);
                terrainBlocks[x][y] = terrainBlock;

                Box waterBlock = new Box(blockSize, blockSize, 0);
                waterBlock.setMaterial(new PhongMaterial(WATER_COLOR));
                waterBlock.setTranslateX(centerX + x * blockSize);
                waterBlock.setTranslateY(centerY + y * blockSize);
                waterBlocks[x][y] = waterBlock;
            }
        }
        updateWater();
    }

    /**
     * Aktualizuje 3D reprezentaci vody.
     */
    public void updateWater() {
        for (int i = 0; i < terrain.getWidth(); i++) {
            for (int j = 0; j < terrain.getHeight(); j++) {
                Box waterBlock = waterBlocks[i][j];
                double water = terrain.getWaterAt(i, j);
                if (water < MIN_VISIBLE_WATER_LEVEL) {
                    waterBlock.setVisible(false);
                } else {
                    waterBlock.setVisible(true);
                    double waterBlockDepth = water * SCALE;
                    waterBlock.setDepth(waterBlockDepth);
                    double terrainOffset = terrainBlocks[i][j].getTranslateZ() * 2;
                    waterBlock.setTranslateZ(terrainOffset - waterBlockDepth/2 - GAP);
                }
            }
        }
    }

    /**
     * Přidá všechny 3D objekty do skupiny.
     *
     * @param group skupina
     */
    public void addToGroup(Group group) {
        for (Box[] boxes : waterBlocks) {
            group.getChildren().addAll(boxes);
        }
        for (Box[] boxes : terrainBlocks) {
            group.getChildren().addAll(boxes);
        }
        group.getChildren().add(pedestal);
    }

    public Box[][] getTerrainBlocks() {
        return terrainBlocks;
    }

    public Box[][] getWaterBlocks() {
        return waterBlocks;
    }

    public Box getPedestal() {
        return pedestal;
    }
}
