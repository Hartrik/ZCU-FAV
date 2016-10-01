package p1;

/**
 *
 * @version 2016-02-24
 * @author Patrik Harag
 */
public class Zamestnanci {

    private static void tiskVzestupne(Zamestnanec prvni) {
        if (prvni != null) {
            System.out.println("\nVzestupne dle mzdy:");
            Zamestnanec zamestnanec = prvni;
            do {
                System.out.println(zamestnanec);
            } while ((zamestnanec = zamestnanec.dalsi) != prvni);
        } else {
            System.out.println("Seznam je prazdny.");
        }
    }

    private static void tiskSestupne(Zamestnanec prvni) {
        if (prvni != null) {
            System.out.println("\nSestupne dle mzdy:");
            Zamestnanec zamestnanec = prvni.predchozi;
            do {
                System.out.println(zamestnanec);
            } while ((zamestnanec = zamestnanec.predchozi) != prvni.predchozi);
        } else {
            System.out.println("Seznam je prazdny.");
        }
    }

    public static void main(String[] arrstring) {
        Iterator it = new Iterator(null);

        it.pridej("Pavelka", 18000);
        it.pridej("Janota", 50000);
        it.pridej("Zelenka", 25000);
        it.pridej("Novák", 65000);
        it.pridej("Pavlica", 15000);

        tiskVzestupne(it.prvni());
        tiskSestupne(it.prvni());
    }

}