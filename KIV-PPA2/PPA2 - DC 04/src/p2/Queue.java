package p2;

import java.util.NoSuchElementException;

/**
 *
 * @version 2016-03-17
 * @author Patrik Harag
 */
public class Queue <T> {

    private static final int INITIAL_CAPACITY = 2;
    private static final int RESIZE_RATIO = 3;

    private Object[] array = new Object[INITIAL_CAPACITY];
    private int capacity = INITIAL_CAPACITY;
    private int size = 0;
    private int first = -1;
    private int index = 0;

    public void enqueue(T obj) {
        if (isEmpty()) {
            // pokud se fronta zcela vyprázdnila, index se posune na začátek
            first = -1;
            index = 0;
        }

        if (first == index)
            increaseCapacity();

        array[index] = obj;
        if (first == -1)
            first = index;

        index++;
        if (index == capacity)
            index = 0;

        size++;
    }

    private void increaseCapacity() {
        array = copyArray(capacity * RESIZE_RATIO);
        first = 0;
        index = size;
        capacity = array.length;
    }

    public T dequeue() {
        if (isEmpty())
            throw new NoSuchElementException();

        @SuppressWarnings("unchecked")
        T obj = (T) array[first];
        array[first] = null;

        first++;
        if (first == capacity)
            first = 0;

        size--;

        if (size >= INITIAL_CAPACITY && capacity / RESIZE_RATIO == size)
            compact();

        return obj;
    }

    private void compact() {
        array = copyArray(size);
        capacity = size;
        first = 0;
        index = 0;
    }

    private Object[] copyArray(int newSize) {
        Object[] newArray = new Object[newSize];

        int j = first;
        for (int i = 0; i < newSize; i++) {
            if (j == capacity)
                j = 0;

            newArray[i] = array[j];
            j++;
        }
        return newArray;
    }

    public Object[] getArray() {
        return copyArray(size);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

}