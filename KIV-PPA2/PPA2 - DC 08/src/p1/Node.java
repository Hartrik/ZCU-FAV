package p1;

/**
 *
 *
 * @version 2016-03-22
 * @author Patrik Harag
 */
public class Node {

    public final String key;
    public final String value;

    public Node next;

    public Node(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
