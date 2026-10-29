package ru.aston.teamproject.input;
import ru.aston.teamproject.factory.DataFactory;
import ru.aston.teamproject.validation.InputValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JsonInputStrategy<T> implements DataInputStrategy<T> {
    private final Path file;
    private final DataFactory<T> factory;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonInputStrategy(Path file, DataFactory<T> factory) {
        this.file = Objects.requireNonNull(file);
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public List<T> read(int count) {
        InputValidator.nonNegativeCount(count);
        JsonNode root;
        try {
            root = mapper.readTree(file.toFile());
        } catch (IOException e) {
            throw new IllegalArgumentException("Не удалось прочитать JSON: " + file, e);
        }
        if (root == null || !root.isArray()) {
            throw new IllegalArgumentException("Корень JSON должен быть массивом объектов");
        }
        if (root.size() < count) {
            throw new IllegalArgumentException("В JSON только " + root.size() + " записей, требуется " + count);
        }
        List<T> result = new ArrayList<>(count);
        Set<String> allowed = new HashSet<>(factory.fieldNames());
        for (int i = 0; i < count; i++) {
            JsonNode node = root.get(i);
            if (!node.isObject()) {
                throw new IllegalArgumentException("Запись " + (i + 1) + " должна быть объектом");
            }
            var names = node.fieldNames();
            while (names.hasNext()) {
                String name = names.next();
                if (!allowed.contains(name)) {
                    throw new IllegalArgumentException("Запись " + (i + 1) + ": неизвестное поле " + name);
                }
            }
            Map<String, String> fields = new LinkedHashMap<>();
            for (String name : factory.fieldNames()) {
                JsonNode value = node.get(name);
                if (value == null || value.isNull() || !(value.isTextual() || value.isIntegralNumber())) {
                    throw new IllegalArgumentException("Запись " + (i + 1) + ": неверный тип или отсутствие поля " + name);
                }
                fields.put(name, value.asText());
            }
            try {
                result.add(factory.fromFields(fields));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Запись " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        return result;
    }
}
