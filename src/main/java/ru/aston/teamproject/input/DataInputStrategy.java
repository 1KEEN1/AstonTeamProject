package ru.aston.teamproject.input;

import ru.aston.teamproject.collection.SimpleList;

public interface DataInputStrategy<T> {
    SimpleList<T> read(int count);
}
