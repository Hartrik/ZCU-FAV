package cz.zcu.kiv.pro.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Třída představuje jeden uzel ve stromu.
 *
 * @version 2016-11-20
 * @author Patrik Harag
 */
public class TreeNode {

    private final int id;
    private TreeNode predecessor;
    private final List<TreeNode> successors;

    private double x;
    private double y;

    private double temp;

    public TreeNode(int id) {
        this(id, new ArrayList<>(2));
    }

    public TreeNode(int id, List<TreeNode> successors) {
        this.id = id;
        this.successors = new ArrayList<>(successors);
    }

    public List<TreeNode> getSuccessors() {
        return Collections.unmodifiableList(successors);
    }

    public TreeNode getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(TreeNode predecessor) {
        this.predecessor = predecessor;
    }

    public void addSuccessor(TreeNode node) {
        successors.add(node);
    }

    public boolean isLeaf() {
        return successors.isEmpty();
    }

    public int getId() {
        return id;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TreeNode other = (TreeNode) obj;
        return this.id == other.id;
    }

    public void walk(Consumer<TreeNode> consumer) {
        consumer.accept(this);
        for (TreeNode successor : getSuccessors()) {
            successor.walk(consumer);
        }
    }

    @Override
    public String toString() {
        String list = successors.stream()
                .map(n -> n.getId() + "")
                .collect(Collectors.joining(", ", "[", "]"));

        return "TreeNode{" + "id=" + id + ", x=" + x + ", y=" + y
                + ", successors=" + list + '}';
    }

}