package ru.aston.teamproject.output;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileResultWriterTest {
    @TempDir
    Path tempDir;

    private static final String NL = System.lineSeparator();

    private final FileResultWriter<String> writer = new FileResultWriter<>();

    private Path target() {
        return tempDir.resolve("result.txt");
    }

    private String read(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    //null
    @Test
    @DisplayName("null-path недопустим")
    void whenPathIsNull_thenThrowsNullPointerException(){
        assertThatThrownBy(()-> writer.append(null, List.of("a"), Function.identity()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("null-items недопустим")
    void whenItemsIsNull_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> writer.append(target(), null, Function.identity()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("null-formatter недопустим")
    void whenFormatterIsNull_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> writer.append(target(), List.of("a"), null))
                .isInstanceOf(NullPointerException.class);
    }

    // Запись

    @Test
    @DisplayName("Один элемент добавляет одну строку с lineSeparator")
    void whenOneItem_thenWritesOneLine() throws IOException {
        Path file = target();

        writer.append(file, List.of("alpha"), Function.identity());

        assertThat(read(file)).isEqualTo("alpha" + NL);
    }

    @Test
    @DisplayName("Несколько элементов создают по строке на элемент, порядок сохранён")
    void whenMultipleItems_thenWritesEachOnItsOwnLine() throws IOException {
        Path file = target();

        writer.append(file, List.of("a", "b", "c"), Function.identity());

        assertThat(read(file)).isEqualTo("a" + NL + "b" + NL + "c" + NL);
    }

    @Test
    @DisplayName("Пустой список создает пустой файл")
    void whenItemsIsEmpty_thenCreatesEmptyFile() throws IOException {
        Path file = target();

        writer.append(file, List.of(), Function.identity());

        assertThat(Files.exists(file)).isTrue();
        assertThat(read(file)).isEmpty();
    }

    //append

    @Test
    @DisplayName("Второй вызов дописывает, а не перезаписывает")
    void whenAppendTwice_thenContentIsConcatenated() throws IOException {
        Path file = target();

        writer.append(file, List.of("a", "b"), Function.identity());
        writer.append(file, List.of("c"), Function.identity());

        assertThat(read(file)).isEqualTo("a" + NL + "b" + NL + "c" + NL);
    }

    @Test
    @DisplayName("Файл создаётся, если его не было")
    void whenFileDoesNotExist_thenCreatesIt() throws IOException {
        Path file = target();
        assertThat(Files.exists(file)).isFalse();

        writer.append(file, List.of("x"), Function.identity());

        assertThat(Files.exists(file)).isTrue();
    }

    //форматтер

    @Test
    @DisplayName("Форматтер применяется к каждому элементу")
    void whenFormatterProvided_thenAppliedToEachItem() throws IOException {
        FileResultWriter<Integer> intWriter = new FileResultWriter<>();
        Path file = target();

        intWriter.append(file, List.of(1, 2, 3), n -> "n=" + n);

        assertThat(read(file)).isEqualTo("n=1" + NL + "n=2" + NL + "n=3" + NL);
    }

    @Test
    @DisplayName("Форматтер вернул null → NPE")
    void whenFormatterReturnsNull_thenThrowsNullPointerException() {
        Path file = target();

        assertThatThrownBy(() -> writer.append(file, List.of("a"), x -> null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Форматтер бросил исключение → пробрасывается наружу")
    void whenFormatterThrows_thenExceptionPropagates() {
        Path file = target();
        IllegalStateException boom = new IllegalStateException("boom");

        assertThatThrownBy(() -> writer.append(file, List.of("a"), x -> { throw boom; }))
                .isSameAs(boom);
    }

    @Test
    @DisplayName("null-элемент передаётся в форматтер")
    void whenItemIsNull_thenFormatterReceivesNull() throws IOException {
        Path file = target();

        writer.append(file, java.util.Arrays.asList((String) null), x -> x == null ? "<null>" : x);

        assertThat(read(file)).isEqualTo("<null>" + NL);
    }

    // IO
    @Test
    @DisplayName("Путь в несуществующую директорию → IOException")
    void whenParentDirectoryMissing_thenThrowsIOException() {
        Path bad = tempDir.resolve("no-such-dir").resolve("result.txt");

        assertThatThrownBy(() -> writer.append(bad, List.of("a"), Function.identity()))
                .isInstanceOf(IOException.class);
    }

    // Кодировка
    @Test
    @DisplayName("UTF-8: кириллица и эмодзи сохраняются")
    void whenContentHasNonAscii_thenWrittenInUtf8() throws IOException {
        Path file = target();
        String text = "Привет, мир! 😀";

        writer.append(file, List.of(text), Function.identity());

        assertThat(read(file)).isEqualTo(text + NL);
    }
}
