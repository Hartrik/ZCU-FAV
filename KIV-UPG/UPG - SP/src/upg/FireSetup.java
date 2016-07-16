package upg;

/**
 * Přepravka pro nastavení střelby.
 *
 * @version 2016-05-03
 * @author Patrik Harag
 */
public class FireSetup {

    public double angle;
    public double elevation;
    public double velocity;
    public Vec wind;

    public FireSetup(double angle, double elevation, double velocity, Vec wind) {
        this.angle = angle;
        this.elevation = elevation;
        this.velocity = velocity;
        this.wind = wind;
    }

}