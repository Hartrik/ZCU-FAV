package p1;

/**
 * Představuje jeden uzel v binárním stromu.
 *
 * @version 2016-03-05
 * @author Patrik Harag
 */
public class Node {

    public final int data;
    public Node left;
    public Node right;

    public Node(int data) {
        this.data = data;
    }

}