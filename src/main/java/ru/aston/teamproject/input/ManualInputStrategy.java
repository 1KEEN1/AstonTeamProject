package ru.aston.teamproject.input;

import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.DataFactory;
import ru.aston.teamproject.validation.InputValidator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class ManualInputStrategy<T> implements DataInputStrategy<T> {
    private final Scanner scanner;
    private final DataFactory<T> factory;

    public ManualInputStrategy(Scanner scanner, DataFactory<T> factory) {
        this.scanner = java.util.Objects.requireNonNull(scanner);
        this.factory = java.util.Objects.requireNonNull(factory);
    }

    @Override
    public SimpleList<T> read(int count) {
        InputValidator.nonNegativeCount(count);

        return IntStream.range(0, count)
                .mapToObj(i->readVal(i+1, count))
                .collect(Collector.of(
                        () -> new SimpleArrayList<T>(count),
                        SimpleArrayList::add,
                        (SimpleArrayList<T> left, SimpleArrayList<T> right)->{
                            right.forEach(left::add);
                            return left;
                        }
                ));
    }

    private T readVal(int index, int count) {
        while (true) {
            Map<String, String> fields = new LinkedHashMap<>();
            System.out.println("Input object " + index + " from " + count);
            for (String field : factory.fieldNames()) {
                System.out.print(field + ": ");
                try {
                    fields.put(field, scanner.nextLine());
                } catch (NoSuchElementException e) {
                    throw new IllegalStateException("Input stream completed before the collection was filled", e);
                }
            }
            try {
                return (factory.fromFields(fields));
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + ". Re-enter the object.");
            }
        }
    }
}

