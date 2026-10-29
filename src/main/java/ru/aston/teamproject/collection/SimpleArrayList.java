package ru.aston.teamproject.collection;

import java.util.*;

public class SimpleArrayList<T> implements SimpleList<T> {

    private T[] container;
    private int size;
    private int modCount;

    @SuppressWarnings("unchecked")
    public SimpleArrayList(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity: " + capacity);
        this.container = (T[]) new Object[capacity];
    }

    @Override
    public void add(T value) {
        expandList();
        container[size++] = value;
        modCount++;
    }

    @Override
    public T set(int index, T newValue) {
        T tempElement = get(index);
        container[index] = newValue;
        return tempElement;
    }

    @Override
    public T remove(int index) {
        T tempElement = get(index);
        System.arraycopy(container, index + 1, container, index, size - index - 1);
        container[size - 1] = null;
        size--;
        modCount++;
        return tempElement;
    }

    @Override
    public T get(int index) {
        Objects.checkIndex(index, size);
        return container[index];
    }

    @Override
    public int size() {
        return size;
    }

    private void expandList() {
        if (size == container.length) {
            int newCapacity = container.length == 0 ? 1 : container.length * 2;
            container = Arrays.copyOf(container, newCapacity);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            private int tempMod = modCount;

            private void checkForComodification() {
                if (tempMod != modCount) {
                    throw new ConcurrentModificationException();
                }
            }

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                checkForComodification();
                if (index >= size) {
                    throw new NoSuchElementException();
                }
                return container[index++];
            }
        };
    }
}