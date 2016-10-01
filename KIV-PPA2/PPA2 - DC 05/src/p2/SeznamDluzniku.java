package p2;

/**
 *
 * @version 2016-03-18
 * @author Patrik Harag
 */
public class SeznamDluzniku {

    private Dluh dluhy;

    public SeznamDluzniku pridat(String jmeno, int dluh) {
        if (jePrazdne())
            dluhy = new Dluh(jmeno, dluh);
        else
            dluhy = new Dluh(jmeno, dluh, dluhy);

        return this;
    }

    public void propojit() {
        if (jePrazdne()) return;

        Dluh polozka = dluhy;

        do {
            if (polozka.dalsiTento != null)
                continue;

            Dluh propojit = polozka;
            Dluh vnitrni = polozka;

            while ((vnitrni = vnitrni.dalsi) != null) {
                if (polozka.jmeno.equals(vnitrni.jmeno))
                    propojit = (propojit.dalsiTento = vnitrni);
            }

        } while ((polozka = polozka.dalsi) != null);
    }

    public int secti() {
        if (jePrazdne()) return 0;

        Dluh polozka = dluhy;
        int soucet = 0;

        do {
            soucet += polozka.penize;
        } while ((polozka = polozka.dalsi) != null);

        return soucet;
    }

    public int secti(String jmeno) {
        if (jePrazdne()) return 0;

        Dluh polozka = najdiPrvni(jmeno);
        if (polozka == null)
            return 0;

        int soucet = 0;

        do {
            soucet += polozka.penize;
        } while ((polozka = polozka.dalsiTento) != null);

        return soucet;
    }

    private Dluh najdiPrvni(String jmeno) {
        if (jePrazdne()) return null;

        Dluh polozka = dluhy;
        do {
            if (polozka.jmeno.equals(jmeno))
                return polozka;
        } while ((polozka = polozka.dalsi) != null);

        return null;
    }

    public boolean jePrazdne() {
        return (dluhy == null);
    }

}