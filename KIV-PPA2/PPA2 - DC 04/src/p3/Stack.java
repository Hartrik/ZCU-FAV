package p3;

import java.util.NoSuchElementException;

/**
 *
 * @version 2016-03-11
 * @author Patrik Harag
 */
public class Stack <T> {

    private static final int INITIAL_CAPACITY = 2;
    private static final int RESIZE_RATIO = 2;

    private Object[] array = new Object[INITIAL_CAPACITY];
    private int size = 0;

    public void push(T obj) {
        increaseSizeIfNecessary(size + 1);

        array[size] = obj;
        size++;
    }

    private void increaseSizeIfNecessary(int newSize) {
        if (newSize > array.length) {
            // zvětšení pole
            Object[] newArray = new Object[size * RESIZE_RATIO];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
    }

    public T pop() {
        if (isEmpty()) throw new NoSuchElementException();

        size--;

        @SuppressWarnings("unchecked")
        T obj = (T) array[size];
        array[size] = null;

        decreaseSizeIfPossible(size);

        return obj;
    }

    private void decreaseSizeIfPossible(int newSize) {
        if (newSize >= INITIAL_CAPACITY
                && array.length / RESIZE_RATIO == newSize) {

            // zmenšení pole
            Object[] newArray = new Object[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            array = newArray;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

}