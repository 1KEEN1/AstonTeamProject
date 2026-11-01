package ru.aston.teamproject.input;

import java.util.List;

public interface DataInputStrategy<T> {
    List<T> read(int count);
}
