package p1;

/**
 *
 * @version 2016-02-24
 * @author Patrik Harag
 */
public class Iterator {

    private Zamestnanec prvni;
    private Zamestnanec aktualni;

    public Iterator(Zamestnanec prvni) {
        this.prvni = prvni;
        this.aktualni = prvni;
    }

    // požadované metody

    public void naZacatek() {
        this.aktualni = prvni;
    }

    public boolean jePrvni() {
        return aktualni == prvni;
    }

    public boolean jePosledni() {
        return aktualni == prvni.predchozi;
    }

    public void naDalsiPrvek() {
        if (!prazdne())
            this.aktualni = aktualni.dalsi;
    }

    public void naPredchoziPrvek() {
        if (!prazdne())
            this.aktualni = aktualni.predchozi;
    }

    public void vlozZa(String jmeno, int mzda) {
        Zamestnanec zamestnanec = new Zamestnanec(jmeno, mzda);

        if (prazdne()) {
            this.prvni = zamestnanec;
            this.aktualni = zamestnanec;
            zamestnanec.predchozi = zamestnanec.dalsi = zamestnanec;
        } else {
            zamestnanec.dalsi = aktualni.dalsi;
            zamestnanec.dalsi.predchozi = zamestnanec;
            this.aktualni.dalsi = zamestnanec;
            zamestnanec.predchozi = this.aktualni;
            this.aktualni = zamestnanec;
        }
    }

    public void vlozPred(String jmeno, int mzda) {
        Zamestnanec zamestnanec = new Zamestnanec(jmeno, mzda);

        if (prazdne()) {
            this.prvni = zamestnanec;
            prvni.dalsi = prvni.predchozi = zamestnanec;
        } else {
            if (jePrvni()) {
                this.prvni = zamestnanec;
            }
            zamestnanec.dalsi = this.aktualni;
            zamestnanec.predchozi = zamestnanec.dalsi.predchozi;
            this.aktualni.predchozi = zamestnanec;
            zamestnanec.predchozi.dalsi = zamestnanec;
            this.aktualni = zamestnanec;
        }
    }

    // dalsi metody

    public boolean prazdne() {
        return prvni == null;
    }

    public void pridej(String jmeno, int mzda) {
        if (prazdne()) {
            vlozZa(jmeno, mzda);
        } else {
            naZacatek();
            while (aktualni.mzda < mzda) {
                naDalsiPrvek();
                if (jePosledni()) break;
            }

            if (aktualni.mzda <= mzda) {
                vlozZa(jmeno, mzda);
            } else {
                vlozPred(jmeno, mzda);
            }
        }
    }

    public Zamestnanec prvni() {
        return prvni;
    }

}