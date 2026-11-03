package ru.aston.teamproject.input;

import ru.aston.teamproject.collection.SimpleList;

import java.util.ArrayList;
import java.util.List;

public class TestCollections {
    private TestCollections() {}

    public static <T> List<T> toList(SimpleList<T> simple) {
        List<T> result = new ArrayList<>(simple.size());
        for (T item : simple) {   // SimpleList extends Iterable — for-each работает
            result.add(item);
        }
        return result;
    }
}
