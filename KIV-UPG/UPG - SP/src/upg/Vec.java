package upg;

/**
 * 3D vektor.
 *
 * @version 2016-05-03
 * @author Patrik Harag
 */
public class Vec {

    public Vec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec() { }

    public double x;
    public double y;
    public double z;

    @Override
    public String toString() {
        return "Vec{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    public static Vec countVector(double angle, double elevation, double velocity) {
        double distance = velocity * Math.cos(Math.toRadians(elevation));

        return new Vec(
                distance * Math.cos(Math.toRadians(-angle)),
                distance * Math.sin(Math.toRadians(-angle)),
                velocity * Math.sin(Math.toRadians(elevation))
        );
    }

}