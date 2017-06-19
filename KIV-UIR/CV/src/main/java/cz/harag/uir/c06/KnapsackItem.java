package cz.harag.uir.c06;

/**
 *
 * @author Patrik Harag
 * @version 2017-03-29
 */
public class KnapsackItem {

    private final int mass;
    private final int price;

    public KnapsackItem(int mass, int price) {
        this.mass = mass;
        this.price = price;
    }

    public int getMass() {
        return mass;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KnapsackItem item = (KnapsackItem) o;

        return mass == item.mass && price == item.price;
    }

    @Override
    public int hashCode() {
        int result = mass;
        result = 31 * result + price;
        return result;
    }

    @Override
    public String toString() {
        return "{mass=" + mass + ", price=" + price + '}';
    }

}
