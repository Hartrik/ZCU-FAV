package cz.hartrik.ppa1.sort.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Objekt uchovávající tříděná data. Řadící algoritmy manipulují s daty jen
 * prostřednictvím této třídy.
 *
 * @version 2015-10-28
 * @author Patrik Harag
 * @param <T> typ dat
 */
public class Data<T extends Comparable<T>> {

    private final List<T> list;
    private BiConsumer<Integer, Integer> onSwap = (i, j) -> {};
    private BiConsumer<Integer, Integer> onInsert = (i, j) -> {};

    /**
     * Vytvoří novou instanci.
     *
     * @param values hodnoty
     */
    public Data(Collection<T> values) {
        this.list = new LinkedList<>(values);
    }

    /**
     * Porovná dva prvky.
     *
     * @param i index prvního prvku
     * @param j index druhého prvku
     * @return int
     * @see Comparable#compareTo(Object)
     */
    public int compare(int i, int j) {
        return list.get(i).compareTo(list.get(j));
    }

    /**
     * Prohodí dva elementy.
     *
     * @param i index prvního prvku
     * @param j index druhého prvku
     */
    public void swap(int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);

        onSwap.accept(i, j);
    }

    /**
     * Vloží element na určitou pozici, ostatní elementy se posunou směrem k
     * prázdnému místu.
     *
     * @param i index prvku
     * @param j index pozice, na kterou bude prvek vložen
     */
    public void insert(int i, int j) {
        list.add(j, list.get(i));
        list.remove((i < j) ? i : i + 1);

        onInsert.accept(i, j);
    }

    /**
     * Nastaví nový listener, který bude volán po každém provedeném prohození
     * dvou prvků.
     *
     * @param function funkce
     */
    public void setOnSwap(BiConsumer<Integer, Integer> function) {
        this.onSwap = function;
    }

    /**
     * Nastaví nový listener, který bude volán po každém provedeném vložení.
     *
     * @param function funkce
     */
    public void setOnInsert(BiConsumer<Integer, Integer> function) {
        this.onInsert = function;
    }

    /**
     * Vrátí seznam hodnot.
     *
     * @return neměnný seznam
     */
    public List<T> list() {
        return Collections.unmodifiableList(list);
    }

    /**
     * Vrátí počet hodnot.
     *
     * @return počet hodnot
     */
    public int size() {
        return list.size();
    }

}