package p2;

/**
 *
 * @version 2016-03-18
 * @author Patrik Harag
 */
public class Dluh {

    public final String jmeno;
    public final int penize;

    public Dluh dalsi;
    public Dluh dalsiTento;

    public Dluh(String jmeno, int penize) {
        this(jmeno, penize, null);
    }

    public Dluh(String jmeno, int penize, Dluh dalsi) {
        this.jmeno = jmeno;
        this.penize = penize;
        this.dalsi = dalsi;
    }

}