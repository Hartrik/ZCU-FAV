package p3;

/**
 *
 * @version 2016-02-26
 * @author Patrik Harag
 */
public class Squares {

    private final DrawingTool dt;
    private double angle;
    private double x;
    private double y;

    public Squares(DrawingTool dt) {
        this.dt = dt;
        this.x = 5;
        this.y = 5;
    }

    private void drawSquares(int i, double length) {
        draw(length); angle += 90;
        draw(length); angle += 90;
        draw(length); angle += 90;
        draw(length); angle += 90;

        move(length / 2);
        angle += 45;

        if (i >= 0) {
            double c = Math.hypot(length/2, length/2);
            drawSquares(i-1, c);
        }
    }

    private void move(double length) {
        x = x + length * Math.cos(angle * Math.PI / 180);
        y = y + length * Math.sin(angle * Math.PI / 180);
    }

    private void draw(double length) {
        double oldX = x;
        double oldY = y;
        move(length);
        dt.line((int) oldX, (int) oldY, (int) x, (int) y);
    }

    public static void main(String[] args) {
        Squares s = new Squares(new DrawingTool(500, 500, true));
        s.drawSquares(50, 490);
    }

}