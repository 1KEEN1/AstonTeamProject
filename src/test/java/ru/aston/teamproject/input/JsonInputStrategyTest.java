package ru.aston.teamproject.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.DataFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JsonInputStrategyTest {
    @TempDir
    Path tempDir;

    private static final List<String> FIELDS = List.of("name", "password", "mail", "age");

    //фейковая фабрика
    private static DataFactory<String> factoryWithFields() {
        return new DataFactory<>() {
            @Override
            public List<String> fieldNames() {
                return FIELDS;
            }

            @Override
            public String fromFields(Map<String, String> fields) {
                return String.join("|", fields.values());
            }

            @Override
            public String random() {
                return "";
            }
        };
    }

    //фейк, падающий при первом вызове
    private static DataFactory<String> throwingFactory() {
        return new DataFactory<>() {
            @Override
            public List<String> fieldNames() {
                return FIELDS;
            }

            @Override
            public String fromFields(Map<String, String> f) {
                throw new IllegalArgumentException("bad user");
            }

            @Override
            public String random() {
                return "";
            }
        };
    }

    private Path writeJson(String content) throws IOException {
        Path file = tempDir.resolve("data.json");
        Files.writeString(file, content);
        return file;
    }

    private static final String VALID_USER =
            "{\"name\":\"Alice\",\"password\":\"p\",\"mail\":\"a@b.com\",\"age\":\"30\"}";

    //тесты конструктора
    @Test
    @DisplayName("Конструктор с null-файлом бросает NPE")
    void whenFileIsNull_thenConstructorThrows() {
        assertThatThrownBy(() -> new JsonInputStrategy<String>(null, factoryWithFields()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Конструктор с null-фабрикой бросает NPE")
    void whenFactoryIsNull_thenConstructorThrows() {
        assertThatThrownBy(() -> new JsonInputStrategy<String>(tempDir.resolve("x.json"), null))
                .isInstanceOf(NullPointerException.class);
    }

    //тесты с count
    @Test
    @DisplayName("read(-1) вызывает IllegalArgumentException")
    void whenCountIsNegative_thenThrows() throws IOException {
        Path file = writeJson("[]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(() -> strategy.read(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("read(0) возвращает пустой список")
    void whenCountIsZero_thenReturnsEmptyList() throws IOException {
        Path file = writeJson("[]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThat(strategy.read(0).size()).isZero();
    }

    //Тесты на чтение файла
    @Test
    @DisplayName("Несуществующий файл вызывает IAE")
    void whenFileDoesNotExist_thenThrows() {
        Path missing = tempDir.resolve("missing.json");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(missing, factoryWithFields());

        assertThatThrownBy(() -> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Не удалось прочитать JSON");

    }

    @Test
    @DisplayName("Невалидный JSON → IAE с cause")
    void whenJsonIsInvalid_thenThrows() throws IOException {
        Path file = writeJson("{not json");
        JsonInputStrategy<String> strategy =
                new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(() -> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Не удалось прочитать JSON")
                .hasCauseInstanceOf(IOException.class);
    }

    //Корень
    @Test
    @DisplayName("Корень не массив → IAE")
    void whenRootIsNotArray_thenThrows() throws IOException {
        Path file = writeJson("{\"a\":1}");
        JsonInputStrategy<String> strategy =
                new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(() -> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Корень JSON должен быть массивом");
    }

    @Test
    @DisplayName("Корень null → IAE")
    void whenRootIsNull_thenThrows() throws IOException {
        Path file = writeJson("null");
        JsonInputStrategy<String> strategy =
                new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(() -> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Корень JSON должен быть массивом");
    }

    //Размер
    @Test
    @DisplayName("Число записей должно совпадать с count")
    void whenNotEnoughRecords_thenThrows() throws IOException {
        Path file = writeJson("["+VALID_USER+"]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(()-> strategy.read(2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("В JSON только 1 записей, требуется 2");
    }

    //Поля
    @Test
    @DisplayName("Поля должны соответсвовать элементам нашей коллекции")
    void whenUnknownField_thenThrows() throws IOException {
        Path file = writeJson(
                "[{\"name\":\"Alice\",\"password\":\"p\",\"mail\":\"a@b.com\",\"age\":\"30\",\"extra\":\"x\"}]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(()-> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("extra");
    }

    @Test
    @DisplayName("Все поля коллекции должны присутствовать")
    void whenFieldMissing_thenThrows() throws IOException {
        Path file = writeJson(
                "[{\"name\":\"Alice\",\"password\":\"p\",\"mail\":\"a@b.com\"}]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(()-> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("age");
    }

    @Test
    @DisplayName("Поле не может быть null")
    void whenFieldIsNull_thenThrows() throws IOException {
        Path file = writeJson(
                "[{\"name\":\"Alice\",\"password\":\"p\",\"mail\":\"a@b.com\", \"age\":null}]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(()-> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("age");
    }

    @Test
    @DisplayName("Поля должны быть верных типов")
    void whenFieldHasWrongType_thenThrows() throws IOException {
        Path file = writeJson(
                "[{\"name\":\"Alice\",\"password\":\"p\",\"mail\":\"a@b.com\", \"age\":true}]");
        JsonInputStrategy<String> strategy = new JsonInputStrategy<>(file, factoryWithFields());

        assertThatThrownBy(()-> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("age");
    }

    //fromFields

    @Test
    @DisplayName("fromFields бросил IAE → оборачивается с номером записи")
    void whenFromFieldsThrows_thenWrappedWithRecordNumber() throws IOException {
        Path file = writeJson("[" + VALID_USER + "]");
        JsonInputStrategy<String> strategy =
                new JsonInputStrategy<>(file, throwingFactory());

        assertThatThrownBy(() -> strategy.read(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Запись 1")
                .hasMessageContaining("bad user")
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    //Happy path

    @Test
    @DisplayName("Валидный JSON возвращает N объектов")
    void whenValidJson_thenReturnsNElements() throws IOException {
        Path file = writeJson("[" + VALID_USER + ","
                + "{\"name\":\"Bob\",\"password\":\"q\",\"mail\":\"b@b.com\",\"age\":\"25\"}]");
        JsonInputStrategy<String> strategy =
                new JsonInputStrategy<>(file, factoryWithFields());

        SimpleList<String> result = strategy.read(2);

        assertThat(TestCollections.toList(result)).containsExactly(
                "Alice|p|a@b.com|30",
                "Bob|q|b@b.com|25");
    }

    @Test
    @DisplayName("Если count меньше количества добавленных элементов, то будут добавлено только count элементов")
    void whenCountLessThanSize_thenReadsOnlyN() throws IOException {
        Path file = writeJson("[" + VALID_USER + ","
                + "{\"name\":\"Bob\",\"password\":\"q\",\"mail\":\"b@b.com\",\"age\":\"25\"}]");
        JsonInputStrategy<String> strategy =
                new JsonInputStrategy<>(file, factoryWithFields());

        SimpleList<String> result = strategy.read(1);

        assertThat(TestCollections.toList(result)).containsExactly("Alice|p|a@b.com|30");
    }
}
