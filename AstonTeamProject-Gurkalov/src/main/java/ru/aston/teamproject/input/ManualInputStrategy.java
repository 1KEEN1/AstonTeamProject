package ru.aston.teamproject.input;
import ru.aston.teamproject.factory.DataFactory;
import ru.aston.teamproject.validation.InputValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ManualInputStrategy<T> implements DataInputStrategy<T> {
    private final Scanner scanner;
    private final DataFactory<T> factory;

    public ManualInputStrategy(Scanner scanner, DataFactory<T> factory) {
        this.scanner = java.util.Objects.requireNonNull(scanner);
        this.factory = java.util.Objects.requireNonNull(factory);
    }

    @Override
    public List<T> read(int count) {
        InputValidator.nonNegativeCount(count);
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            while (true) {
                Map<String, String> fields = new LinkedHashMap<>();
                System.out.println("Ввод объекта " + (i + 1) + " из " + count);
                for (String field : factory.fieldNames()) {
                    System.out.print(field + ": ");
                    try {
                        fields.put(field, scanner.nextLine());
                    } catch (NoSuchElementException e) {
                        throw new IllegalStateException("Поток ввода завершён до заполнения коллекции", e);
                    }
                }
                try {
                    result.add(factory.fromFields(fields));
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Повторите ввод объекта.");
                }
            }
        }
        return result;
    }
}
