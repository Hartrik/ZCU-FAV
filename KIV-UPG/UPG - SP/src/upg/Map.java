package upg;

/**
 * Představuje herní mapu.
 *
 * @version 2016-02-20
 * @author Patrik Harag
 */
public abstract class Map {

    protected int width;
    protected int height;

    protected int playerX;
    protected int playerY;

    protected int targetX;
    protected int targetY;

    protected int[][] heightMap;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public int[][] getHeightMap() {
        return heightMap;
    }

    @Override
    public String toString() {
        return "Map{"
                + "width=" + width
                + ", height=" + height
                + ", playerX=" + playerX
                + ", playerY=" + playerY
                + ", targetX=" + targetX
                + ", targetY=" + targetY + '}';
    }

}