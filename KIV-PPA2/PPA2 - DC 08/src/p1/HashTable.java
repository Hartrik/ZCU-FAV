package p1;

import java.io.PrintStream;
import java.util.function.BiFunction;

/**
 *
 * @version 2016-03-22
 * @author Patrik Harag
 */
public class HashTable {

    private final BiFunction<String, Integer, Integer> hashFunction;

    private final Node[] array;
    private final int capacity;

    public HashTable(int capacity, BiFunction<String, Integer, Integer> func) {
        this.capacity = capacity;
        this.hashFunction = func;
        this.array = new Node[capacity];
    }

    public void add(String key, String value) {
        int hash = hashFunction.apply(key, capacity);
        Node node = new Node(key, value);

        Node atPosition = this.array[hash];
        if (atPosition != null)
            node.next = atPosition;

        this.array[hash] = node;
    }

    public Node find(String key) {
        int hash = hashFunction.apply(key, capacity);
        Node node = array[hash];

        do {
            if (node.key.equals(key))
                return node;
        } while ((node = node.next) != null);

        return null;
    }

    public void print(PrintStream stream) {
        for (int i = 0; i < array.length; ++i) {
            Node node = array[i];

            stream.printf("%n[%d] ", i);
            if (node != null) {
                do {
                    stream.print(node.value + " ");
                } while ((node = node.next) != null);

            } else {
                stream.print("<null>");
            }
        }
    }

}