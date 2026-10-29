package ru.aston.teamproject.factory;

import java.util.List;
import java.util.Map;

public interface DataFactory<T> {
    List<String> fieldNames();
    T fromFields(Map<String, String> fields);
    T random();
}
