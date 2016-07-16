package upg;

/**
 * Počítá dráhu střely.
 *
 * @version 2016-04-02
 * @author Patrik Harag
 */
public class TrajectoryCounter {

    private Vec p;
    private Vec v;

    private final Vec g;
    private final Vec w;
    private final double c;

    private final double dt;
    private double t;

    /**
     * Inicializuje balistický počítač.
     *
     * @param p třírozměrný polohový vektor střely v čase t=0
     * @param v třírozměrný vektor rychlosti střely v čase t=0
     * @param g třírozměrný vektor gravitačního zrychlení
     * @param w třírozměrný vektor rychlosti větru
     * @param c koeficient vlivu větru/odporu vzduchu
     * @param dt integrační krok v čase
     */
    public TrajectoryCounter(Vec p, Vec v, Vec g, Vec w, double c, double dt) {
        this.p = p;
        this.v = v;
        this.g = g;
        this.w = w;
        this.c = c;
        this.dt = dt;
    }

    /**
     * Vypočítá další pozici.
     *
     * @return třírozměrný polohový vektor střely v čase t
     */
    public Vec nextPosition() {
        t += dt;

        this.p = countPosition();
        this.v = countVelocity();

        return p;
    }

    private Vec countPosition() {
        Vec pos = new Vec();
        pos.x = p.x + v.x * dt;
        pos.y = p.y + v.y * dt;
        pos.z = p.z + v.z * dt;
        return pos;
    }

    private Vec countVelocity() {
        Vec velocity = new Vec();
        velocity.x = v.x + g.x * dt - (v.x - w.x) * dt * c;
        velocity.y = v.y + g.y * dt - (v.y - w.y) * dt * c;
        velocity.z = v.z + g.z * dt - (v.z - w.z) * dt * c;
        return velocity;
    }

    /**
     * Vrátí aktuální čas.
     *
     * @return čas od počátku v sekundách
     */
    public double getTime() {
        return t;
    }

    public Vec getPosition() {
        return p;
    }

}