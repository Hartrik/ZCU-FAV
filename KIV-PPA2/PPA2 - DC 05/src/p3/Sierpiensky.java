package p3;

/**
 *
 * @version 2016-02-26
 * @author Patrik Harag
 */
public class Sierpiensky {

    private final DrawingTool dt;
    private double angle;
    private double x;
    private double y;

    public Sierpiensky(DrawingTool dt, int x, int y) {
        this.dt = dt;
        this.x = x;
        this.y = y;
    }

    public static void main(String[] args) {
        int width = 650;
        DrawingTool drawingTool = new DrawingTool(width + 200, width + 100, true);
        Sierpiensky s = new Sierpiensky(drawingTool, 100, width + 50);

        int level = 2;

        // výpočet délky menší přímky
        // není úplně přesný
        double size = width / (Math.pow(2, level + 1));

        // diagonální přímky jsou kratší
        double diag = size / Math.sqrt(2);

        s.drawCurve(size, diag, level);
    }

    public void drawCurve(double size, double diag, int i) {
        for (int j = 0; j < 4; j++) {
            oneSide(size, diag, i);
            left(45);
            draw(diag);
            left(45);
        }
    }

    private void oneSide(double size, double diag, int i) {
        if (i != 0) {
            oneSide(size, diag, i - 1);

            left(45);
            draw(diag);
            left(45);

            oneSide(size, diag, i - 1);

            right(90);
            draw(size);
            right(90);

            oneSide(size, diag, i - 1);

            left(45);
            draw(diag);
            left(45);

            oneSide(size, diag, i - 1);
        }
    }

    // kreslící metody...

    private void left(double a) {
        angle -= a;
    }

    private void right(double a) {
        angle += a;
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

//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException ex) {
//        }
    }

}