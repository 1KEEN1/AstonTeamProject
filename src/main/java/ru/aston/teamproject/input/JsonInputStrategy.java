package ru.aston.teamproject.input;
import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.DataFactory;
import ru.aston.teamproject.validation.InputValidator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class JsonInputStrategy<T> implements DataInputStrategy<T> {
    private final Path file;
    private final DataFactory<T> factory;
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonInputStrategy(Path file, DataFactory<T> factory) {
        this.file = Objects.requireNonNull(file);
        this.factory = Objects.requireNonNull(factory);
    }

    @Override
    public SimpleList<T> read(int count) {
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
        try {
            InputValidator.validateAvailableSpace(count, root.size());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("В JSON только " + root.size() + " записей, требуется " + count, e);
        }

        Set<String> allowed = new HashSet<>(factory.fieldNames());
        return IntStream.range(0, count)
                .mapToObj(i -> parseNode(root.get(i), i+1, allowed))
                .collect(Collector.of(
                        ()->new SimpleArrayList<T>(count),
                        SimpleArrayList::add,
                        (left, right) ->{
                            right.forEach(left::add);
                            return left;
                        }
                ));
    }

    private T parseNode(JsonNode node, int index, Set<String> allowed){
        if (!node.isObject()) {
            throw new IllegalArgumentException("Запись " + index  + " должна быть объектом");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!allowed.contains(name)) {
                throw new IllegalArgumentException("Запись " + index + ": неизвестное поле " + name);
            }
        }
        Map<String, String> fields = new LinkedHashMap<>();
        for (String name : factory.fieldNames()) {
            JsonNode value = node.get(name);
            if (value == null || value.isNull() || !(value.isTextual() || value.isIntegralNumber())) {
                throw new IllegalArgumentException("Запись " + index + ": неверный тип или отсутствие поля " + name);
            }
            fields.put(name, value.asText());
        }
        try {
            return factory.fromFields(fields);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Запись " + index + ": " + e.getMessage(), e);
        }
    }
}
