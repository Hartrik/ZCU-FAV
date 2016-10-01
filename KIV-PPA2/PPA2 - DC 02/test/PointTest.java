
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @version 2016-02-23
 * @author Patrik Harag
 */
public class PointTest {

    @Test
    public void test_1() {
        Point a = new Point(0, 0);
        Point b = new Point(4, 3);
        Point c = new Point(8, 0);

        double ac = a.distance(c);
        double abc = a.distance(b) + b.distance(c);

        assertEquals(2, abc - ac, 0.001);
        assertEquals(323.13, b.angle(c), 0.001);
    }

    @Test
    public void test_2() {
        Point a = new Point(0, 0);
        Point b = new Point(4, 3);
        Point c = new Point(4, 6);

        double ac = a.distance(c);
        double abc = a.distance(b) + b.distance(c);

        assertEquals(0.789, abc - ac, 0.001);
        assertEquals(90.00, b.angle(c), 0.001);
    }

    @Test
    public void testAngleSameX() {
        Point b = new Point(5, 5);
        Point c = new Point(10, 5);

        assertEquals(0.00, b.angle(c), 0.001);
    }

    @Test
    public void testAngle_4() {
        Point b = new Point(0, 0);
        Point c = new Point(1, 1);

        assertEquals(45, b.angle(c), 0.001);
    }

    @Test
    public void testAngle_5() {
        Point b = new Point(0, 0);
        Point c = new Point(-1, 1);

        assertEquals(135, b.angle(c), 0.001);
    }

    @Test
    public void testAngle_6() {
        Point b = new Point(0, 0);
        Point c = new Point(-1, -1);

        assertEquals(225, b.angle(c), 0.001);
    }

}