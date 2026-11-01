package ru.aston.teamproject.output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class FileResultWriter<T> {
    public void append(Path path, List<T> items, Function<T, String> formatter) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(items);
        Objects.requireNonNull(formatter);
        StringBuilder buffer = new StringBuilder();
        for (T item : items) {
            buffer.append(Objects.requireNonNull(formatter.apply(item)))
                    .append(System.lineSeparator());
        }
        Files.writeString(path, buffer, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
