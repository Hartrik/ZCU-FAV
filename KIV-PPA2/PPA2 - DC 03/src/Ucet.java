
/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class Ucet {

    private final String vlastnik;
    private Polozka vybery = new Polozka(0);
    private Polozka vklady = new Polozka(0);

    public Ucet(String vlastnik) {
        this.vlastnik = vlastnik;
    }

    public Ucet operace(int penize) {
        if (penize > 0)
            vklady = new Polozka(+penize, vklady);
        else if (penize < 0)
            vybery = new Polozka(-penize, vybery);

        return this;
    }

    public String getVlastnik() {
        return vlastnik;
    }

    public int sectiVklady() {
        return secti(vklady);
    }

    public int sectiVybery() {
        return secti(vybery);
    }

    private int secti(Polozka polozka) {
        int soucet = 0;

        do {
            soucet += polozka.getPenize();
        } while ((polozka = polozka.getDalsi()) != null);

        return soucet;
    }

    public boolean maDebet() {
        return sectiVklady() - sectiVybery() < 0;
    }

}