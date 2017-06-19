package cz.harag.uir.c06;

import java.util.List;

/**
 * @author Patrik Harag
 * @version 2017-03-29
 */
public class Knapsack {

    private final List<KnapsackItem> items;
    private double fitness;

    public Knapsack(List<KnapsackItem> items) {
        this.items = items;
        this.fitness = Double.NaN;
    }

    public List<KnapsackItem> getItems() {
        return items;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Knapsack knapsack = (Knapsack) o;
        return items != null ? items.equals(knapsack.items) : knapsack.items == null;
    }

    @Override
    public int hashCode() {
        return items != null ? items.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Knapsack{" +
                "fitness=" + fitness +
                ", items=" + items +
                '}';
    }
}
