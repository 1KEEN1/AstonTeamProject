package ru.aston.teamproject.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.DataFactory;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ManualInputStrategyTest {
    private static final List<String> FIELDS = List.of("name", "password", "mail", "age");

    //фейковая фабрика
    private static DataFactory<String> factoryWithFields(List<String> fieldNames) {
        return new DataFactory<>() {
            @Override
            public List<String> fieldNames() {
                return fieldNames;
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

    private static final class FailingThenSucceedingFactory implements DataFactory<String> {
        private final List<String> fields;
        private final int failFirstN;
        private int attempts = 0;

        FailingThenSucceedingFactory(List<String> fields, int failFirstN) {
            this.fields = fields;
            this.failFirstN = failFirstN;
        }

        @Override
        public List<String> fieldNames() {
            return fields;
        }

        @Override
        public String fromFields(Map<String, String> f) {
            if (attempts++ < failFirstN) {
                throw new IllegalArgumentException("bad input #" + attempts);
            }
            return String.join("|", f.values());
        }

        @Override
        public String random() {
            return "";
        }
    }


    private static Scanner scannerOf(String input) {
        return new Scanner(input);
    }

    @Test
    @DisplayName("Конструктор с null-scanner бросает NPE")
    void whenScannerIsNull_thenConstructorThrows() {
        assertThatThrownBy(() -> new ManualInputStrategy<String>(null,
                factoryWithFields(FIELDS)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Конструктор с null-фабрикой бросает NPE")
    void whenFactoryIsNull_thenConstructorThrows() {
        assertThatThrownBy(() -> new ManualInputStrategy<String>(scannerOf(""), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("read(0) вызывает пустой список")
    void whenCountIsZero_thenReturnsEmptyList() {
        ManualInputStrategy<String> strategy = new ManualInputStrategy<>(scannerOf(""),
                factoryWithFields(FIELDS));
        SimpleList<String> result = strategy.read(0);
        assertThat(result.size()).isZero();
    }

    @Test
    @DisplayName("read(-1) бросает IllegalArgumentException")
    void whenCountIsNegative_thenThrows() {
        ManualInputStrategy<String> strategy = new ManualInputStrategy<>(scannerOf(""),
                factoryWithFields(FIELDS));
        assertThatThrownBy(() -> strategy.read(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("read(N) читает N объектов, поля идут в порядке fieldNames()")
    void whenValidInput_thenReadsNElements() {
        Scanner scanner = new Scanner(
                "Alice\nsecret\nalice@example.com\n30\n" +   // объект 1
                        "Bob\nqwerty\nbob@example.com\n25\n");       // объект 2

        ManualInputStrategy<String> strategy = new ManualInputStrategy<>(scanner, factoryWithFields(FIELDS));

        SimpleList<String> result = strategy.read(2);

        assertThat(TestCollections.toList(result)).containsExactly(
                "Alice|secret|alice@example.com|30",
                "Bob|qwerty|bob@example.com|25");
    }

    @Test
    @DisplayName("При IllegalArgumentException ввод повторяется")
    void whenFromFieldsThrows_thenRetriesInput() {
        Scanner scanner = scannerOf(
                "BAD\nBAD\nBAD\nBAD\n" +              // первая попытка — упадёт
                        "Alice\nsecret\nalice@example.com\n30\n"); // вторая — успешна

        ManualInputStrategy<String> strategy =
                new ManualInputStrategy<>(scanner,
                        new FailingThenSucceedingFactory(FIELDS, 1));

        SimpleList<String> result = strategy.read(1);

        assertThat(TestCollections.toList(result)).containsExactly("Alice|secret|alice@example.com|30");
    }

    @Test
    @DisplayName("Если Scanner исчерпан, бросается IllegalStateException")
    void whenScannerExhausted_thenThrowsIllegalStateException() {
        Scanner scanner = scannerOf("Alice\n");   // не хватает 3 строк

        ManualInputStrategy<String> strategy =
                new ManualInputStrategy<>(scanner, factoryWithFields(FIELDS));

        assertThatThrownBy(() -> strategy.read(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Input stream completed");
    }
}
