package sp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import sp.Graph.Node;

/**
 * Třída zajišťující logiku aplikace.
 *
 * @version 2016-04-01
 * @author Patrik Harag
 */
public class Main {

    public static final String OUT_FILE = "vysledky.txt";

    /**
     * Vstupní metoda.
     *
     * @param args argumenty příkazové řádky
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws Exception {
        // args = new String[] { "data_1M.txt" };

        if (args.length > 0) {
            try (PrintStream ps = new PrintStream(OUT_FILE)) {
                process(new Scanner(new File(args[0])), ps);
            }
        } else {
            process(new Scanner(System.in), System.out);
        }
    }

    public static void process(Scanner sc, PrintStream pw) {
        final String graphType = sc.next();
        final int size = sc.nextInt();

        // inicializace
        char mType = graphType.charAt(0);
        String iType = graphType.substring(1);
        Graph<?> graph = graph(mType, iType, size);

        // načtení vrcholů
        for (int i = 0; i < size; i++)
            graph.addNode(sc.next());

        // načtení spojení
        int numberOfConnections = sc.nextInt();
        for (int i = 0; i < numberOfConnections; i++)
            parseConnection(sc.next(), graph);

        // prohledání
        final String n = sc.next();
        List<Node> found = new ArrayList<>();
        graph.search(n, found::add);

        // výpis
        String nodes = found.stream()
                .map(Node::getValue).collect(Collectors.joining(", "));
        pw.printf("%s(%s): %s", iType, n, nodes);
    }

    private static Graph<?> graph(char mType, String iType, int size) {
        if (mType == 'S')
            return iType.equals("BFS") ? new GraphSBFS() : new GraphSDFS();
        else
            return iType.equals("BFS") ? new GraphMBFS(size) : new GraphMDFS(size);
    }

    private static void parseConnection(String text, Graph<?> graph) {
        Pattern pattern = Pattern.compile("(.+)([-|>])(.+)");
        Matcher m = pattern.matcher(text);
        if (m.matches()) {
            String n1 = m.group(1);
            String s = m.group(2);
            String n2 = m.group(3);

            if (s.equals(">"))
                graph.connect(n1, n2);
            else
                graph.connectBidirectional(n1, n2);
        }
    }

}