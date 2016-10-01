
/**
 *
 * @version 2016-02-26
 * @author Patrik Harag
 */
public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double distance(Point other) {
        // Pythagorova věta
        return Math.hypot(getX() - other.getX(), getY() - other.getY());
    }

    public double angle(Point other) {
        double deltaX = other.getX() - getX();
        double deltaY = other.getY() - getY();

        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (angle < 0) ? angle + 360 : angle;
    }

}