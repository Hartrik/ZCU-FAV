
import java.util.stream.Stream;


/**
 *
 * @version 2016-02-19
 * @author Patrik Harag
 */
public class Ucty {

    public static void main(String[] args) {

        // původní

        Ucet[] ucty = {
                new Ucet("Jirka")
                        .operace(+1000)
                        .operace(+1000),
                new Ucet("Petr")
                        .operace(+1000)
                        .operace(-1000)
                        .operace(-1000),
                new Ucet("Pavel")
                        .operace(-1000),
        };

        System.out.println("Debet má:");

        Stream.of(ucty)
                .filter(Ucet::maDebet)
                .map(Ucet::getVlastnik)
                .forEach(System.out::println);

        System.out.println("-------------------------------");

        // rozšířené

        ProvazaneUcty provazaneUcty = new ProvazaneUcty()
                .operace("Jirka", +1000)
                .operace("Petr", +1000)
                .operace("Jirka", +1000)
                .operace("Petr", -1000)
                .operace("Pavel", -1000)
                .operace("Petr", -1000);

        System.out.println("Debet má:");

        provazaneUcty.seznam().stream()
                .filter(Ucet::maDebet)
                .map(Ucet::getVlastnik)
                .forEach(System.out::println);
    }

}