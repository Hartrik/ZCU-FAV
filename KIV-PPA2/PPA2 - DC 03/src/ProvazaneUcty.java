
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class ProvazaneUcty {

    private Uzel ucty;

    public ProvazaneUcty operace(String vlastnik, int penize) {
        for (Ucet ucet : seznam()) {
            if (ucet.getVlastnik().equals(vlastnik)) {
                ucet.operace(penize);
                return this;
            }
        }

        this.ucty = new Uzel() {{
            ucet = new Ucet(vlastnik).operace(penize);
            dalsi = ucty;
        }};

        return this;
    }

    public List<Ucet> seznam() {
        List<Ucet> arrayList = new ArrayList<>();

        if (ucty != null) {
            Uzel uzel = ucty;

            do {
                arrayList.add(uzel.ucet);
            } while ((uzel = uzel.dalsi) != null);
        }

        return arrayList;
    }

    private static class Uzel {
        protected Ucet ucet;
        protected Uzel dalsi;
    }

}