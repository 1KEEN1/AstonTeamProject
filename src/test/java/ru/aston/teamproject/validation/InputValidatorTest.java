package ru.aston.teamproject.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class InputValidatorTest {
    //nonBlank
    @Test
    @DisplayName("nonBlank: возвращает строку без пробелов по краям")
    void whenNonBlank_ThenTrims() {
        assertThat(InputValidator.nonBlank("  Alice  ", "name")).isEqualTo("Alice");
    }

    @Test
    @DisplayName("nonBlank: обычная строка возвращается как есть")
    void whenVAlidStrNonBlank_returnsValue() {
        assertThat(InputValidator.nonBlank("Alice", "name")).isEqualTo("Alice");
    }

    @ParameterizedTest(name = "nonBlank: [{0}] невалиден")
    @ValueSource(strings = {"", "   ", "\t\n"})
    @DisplayName("Пустая/пробельная строка недопустима")
    void whenStrEmptyOrSpaced_thenThrows(String blank) {
        assertThatThrownBy(() -> InputValidator.nonBlank(blank, "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    @DisplayName("Null строка недопустима")
    void whenStrIsNull_thenThrows() {
        assertThatThrownBy(() -> InputValidator.nonBlank(null, "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    // ---------- positiveInt ----------

    @Test
    @DisplayName("positiveInt: положительное число")
    void whenValuePositive_thenItsReturn() {
        assertThat(InputValidator.positiveInt("42", "age")).isEqualTo(42);
    }

    @Test
    @DisplayName("1 — минимально допустимое")
    void whenValueIsOne_thenReturnsOne() {
        assertThat(InputValidator.positiveInt("1", "age")).isEqualTo(1);
    }

    @Test
    @DisplayName("positiveInt: пробелы по краям обрезаются")
    void whenValueHasSpaces_thenReturnsTrimmedInt() {
        assertThat(InputValidator.positiveInt("  42  ", "age")).isEqualTo(42);
    }

    @Test
    @DisplayName("positiveInt: 0 → IAE «больше нуля»")
    void whenValueIsZero_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.positiveInt("0", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("more than 0");
    }

    @Test
    @DisplayName("positiveInt: отрицательное → IAE «больше нуля»")
    void whenValueIsNegative_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.positiveInt("-1", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("more than 0");
    }

    @Test
    @DisplayName("positiveInt: не число → IAE с cause NumberFormatException")
    void whenValueIsNotNumber_thenThrowsIllegalArgumentExceptionWithCause() {
        assertThatThrownBy(() -> InputValidator.positiveInt("abc", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("integer")
                .hasCauseInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("positiveInt: 1.5 → IAE (не целое)")
    void whenValueIsDecimal_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.positiveInt("1.5", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("integer");
    }

    @Test
    @DisplayName("positiveInt: переполнение int → IAE")
    void whenValueOverflowsInt_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.positiveInt("2147483648", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("integer");
    }

    @Test
    @DisplayName("positiveInt: null → IAE (через nonBlank)")
    void whenValueIsNull_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.positiveInt(null, "age"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // nonNegativeCount

    @ParameterizedTest(name = "nonNegativeCount: {0} — допустимо")
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
    @DisplayName("Допустимы неотрицательные значения и 0")
    void whenCountIsNonNegative_thenDoesNotThrow(int count) {
        assertThatCode(() -> InputValidator.nonNegativeCount(count))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "nonNegativeCount: {0} — недопустимо")
    @ValueSource(ints = {-1, Integer.MIN_VALUE})
    @DisplayName("Отрицательные значения недопустимы")
    void whenCountIsNegative_thenThrowsIllegalArgumentException(int count) {
        assertThatThrownBy(() -> InputValidator.nonNegativeCount(count))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    // validateAvailableSpace

    @Test
    @DisplayName("Если число находится в границах вместимости, значит никаких ошибок не будет")
    void whenRequestedFitsAvailable_thenDoesNotThrow() {
        assertThatCode(() -> InputValidator.validateAvailableSpace(5, 10))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Число может находиться на нижней границе вместимости")
    void whenRequestedIsZeroAndAvailableIsZero_thenDoesNotThrow() {
        assertThatCode(() -> InputValidator.validateAvailableSpace(0, 0))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Число может находиться на верхней границе вместимости")
    void whenRequestedEqualsAvailable_thenDoesNotThrow() {
        assertThatCode(() -> InputValidator.validateAvailableSpace(10, 10))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Отрицательные числа недопустимы")
    void whenRequestedIsNegative_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.validateAvailableSpace(-1, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Отрицательные числа недопустим")
    void whenAvailableIsNegative_thenThrowsIllegalStateException() {
        assertThatThrownBy(() -> InputValidator.validateAvailableSpace(5, -1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("available spots");
    }

    @Test
    @DisplayName("Количество элементов не должно превышать заданного размера")
    void whenRequestedExceedsAvailable_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> InputValidator.validateAvailableSpace(11, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not enough space")
                .hasMessageContaining("Available: 10");
    }
}
