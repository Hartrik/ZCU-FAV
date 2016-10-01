package p4;

/**
 *
 * @version 2016-02-20
 * @author Patrik Harag
 */
public class Stravnik {

    private final String jmeno;
    private int hlad;

    public Stravnik(String jmeno, int hlad) {
        this.jmeno = jmeno;
        this.hlad = hlad;
    }

    public String getJmeno() {
        return jmeno;
    }

    public int getHlad() {
        return hlad;
    }

    public void snezChutovku() {
        hlad--;
    }

    public boolean nasycen() {
        return hlad == 0;
    }

}
