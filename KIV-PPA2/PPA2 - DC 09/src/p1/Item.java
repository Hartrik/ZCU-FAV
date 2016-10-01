package p1;

/**
 * Položka v haldě
 *
 * @version 2016-04-17
 * @author Patrik Harag
 */
public class Item {

    public int code;
    public int ammount;
    public int price;

    public Item(int code, int ammount, int price) {
        this.code = code;
        this.ammount = ammount;
        this.price = price;
    }

    @Override
    public String toString() {
        return code + " " + ammount + " " + price;
    }

}