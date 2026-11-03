package ru.aston.teamproject.input;
import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.DataFactory;
import ru.aston.teamproject.validation.InputValidator;

import java.util.Objects;

public class RandomInputStrategy<T> implements DataInputStrategy<T> {
    private final DataFactory<T> factory;

    public RandomInputStrategy(DataFactory<T> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public SimpleList<T> read(int count) {
        InputValidator.nonNegativeCount(count);
        SimpleList<T> result = new SimpleArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(Objects.requireNonNull(factory.random(), "Фабрика вернула null"));
        }
        return result;
    }
}
