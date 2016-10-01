
/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class Polozka {

    private final int penize;
    private Polozka dalsi;

    public Polozka(int penize) {
        this(penize, null);
    }

    public Polozka(int penize, Polozka dalsi) {
        this.penize = penize;
        this.dalsi = dalsi;
    }

    public int getPenize() {
        return penize;
    }

    public Polozka getDalsi() {
        return dalsi;
    }

    public void setDalsi(Polozka dalsi) {
        this.dalsi = dalsi;
    }

}