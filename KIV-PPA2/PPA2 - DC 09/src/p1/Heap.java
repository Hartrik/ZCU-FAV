package p1;

import java.util.function.Consumer;

/**
 * Implementace haldy
 *
 * @version 2016-04-17
 * @author Patrik Harag
 */
public class Heap {

    private final Item[] items;
    private final int capacity;  // maximální velikost
    private int size;            // aktuální velikost

    public Heap(int capacity) {
        this.capacity = capacity;
        this.items = new Item[capacity + 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void forEach(Consumer<Item> consumer) {
        for (int i = 1; i < (size + 1); i++) {
            consumer.accept(items[i]);
        }
    }

    public void add(int code, int ammount, int price) {
        items[++size] = new Item(code, ammount, price);
        sortUp(size);  // zařadí prvek na správné místo
    }

    public Item findCheapest(int minAmmount) {
        while ((items[1].ammount < minAmmount) && (size > 1)) {
            // množství nevyhovuje

            items[1] = items[size];  // na první pozici dáme poslední prvek
                                     // nevyhovující prvek se zahodí
            sortDown(1, size - 1);   // seřadíme - nejlevnější bude první

            items[size] = null;  // zmenšení pole
            --size;
        }

        return (items[1].ammount >= minAmmount)
                ? items[1]
                : null;  // nic nevyhovuje
    }

    /**
     * Bude prvek posouvat v haldě vzhůru, dokud se nezařadí na správné místo.
     * Na vrcholu zůstane prvek s nejnižší cenou.
     *
     * @param i index prvku, který chceme zařadit
     */
    private void sortUp(int i) {
        while ((i > 1) && (items[i / 2].price > items[i].price)) {
            swap(i, i /= 2);
        }
    }

    /**
     * Bude prvek posouvat v haldě směrem dolů, dokud se nezařadí na správné
     * místo. Vybírá vždy prvek s nižší cenou. Na vrcholu tedy zůstane prvek
     * s nejnižší cenou.
     *
     * @param i index prvku, který chceme zařadit
     * @param minIndex poslední prvek, níž už sestupovat nebudeme
     */
    private void sortDown(int i, int minIndex) {
        while (2 * i <= minIndex) {
            int iLeft = 2*i;
            int iRight = 2*i + 1;

            // vybere index, kde se nachází položka s nižší cenou
            // pravý následník nemusí existovat
            int iNext = iLeft;
            if (iRight <= minIndex)  // pravý následník existuje
                if (items[iRight].price < items[iLeft].price)  // má nižší cenu
                    iNext = iRight;

            if (items[i].price <= items[iNext].price)
                return;  // prvek je již zařazen

            swap(i, iNext);
            i = iNext;
        }
    }

    private void swap(int index1, int index2) {
        Item item = items[index1];
        items[index1] = items[index2];
        items[index2] = item;
    }

}