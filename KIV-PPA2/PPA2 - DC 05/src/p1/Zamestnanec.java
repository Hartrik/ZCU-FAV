package p1;

/**
 *
 * @version 2016-02-24
 * @author Patrik Harag
 */
public class Zamestnanec {

    final String jmeno;
    final int mzda;
    Zamestnanec predchozi;
    Zamestnanec dalsi;

    public Zamestnanec(String jmeno, int mzda) {
        this.jmeno = jmeno;
        this.mzda = mzda;
    }

    @Override
    public String toString() {
        return "Zamestnanec{" + "jmeno=" + jmeno + ", mzda=" + mzda + '}';
    }

}
