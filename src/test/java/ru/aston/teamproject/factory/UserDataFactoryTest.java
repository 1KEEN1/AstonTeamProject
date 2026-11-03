package ru.aston.teamproject.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.aston.teamproject.User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDataFactoryTest {
    UserDataFactory factory = new UserDataFactory();

    @Nested
    @DisplayName("Тесты для fieldNames")
    class FieldNameTest{
        @Test
        @DisplayName("FieldNames возвращает необходимые поля fromFields, в том же порядке")
        void whenUseFieldNames_ThenReturnsAllExpectedFieldsInOrder () {
            assertEquals(List.of("name", "password", "mail", "age"), factory.fieldNames());
        }

        @Test
        @DisplayName("Поля класса иммутабельны")
        void whenUseFieldNames_thenFieldsIsImmutable () {
            assertThatThrownBy(() -> factory.fieldNames().add("extra"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Тесты для fromFields")
    class FromFieldsTest{

        //happy path
        @Test
        @DisplayName("Юзер с валидными данными создается корректно")
        void whenFieldsValid_thenUserCreated() {
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "password", "secret",
                    "mail", "alice@example.com",
                    "age", "30");

            User user = factory.fromFields(fields);

            assertThat(user.getName()).isEqualTo("Alice");
            assertThat(user.getPassword()).isEqualTo("secret");
            assertThat(user.getMail()).isEqualTo("alice@example.com");
            assertThat(user.getAge()).isEqualTo(30);
        }

        @Test
        @DisplayName("Юзер с граничными данными создается корректно")
        void whenBoundaryAge_thenUserCreated(){
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "password", "secret",
                    "mail", "alice@example.com",
                    "age", "120");

            assertThat(factory.fromFields(fields).getAge()).isEqualTo(120);
        }

        //negative name
        @Test
        @DisplayName("Name не может быть пустым")
        void whenNameIsEmpty_thenThrowsIllegalArgumentException(){
            Map<String, String> fields = Map.of(
                    "name", "",
                    "password", "secret",
                    "mail", "alice@example.com",
                    "age", "120");

            assertThatThrownBy(()-> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        //negative password
        @Test
        @DisplayName("Password не может быть пустым")
        void whenPasswordIsEmpty_thenThrowsIllegalArgumentException(){
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "password", "",
                    "mail", "alice@example.com",
                    "age", "120");

            assertThatThrownBy(()-> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        //negative mail
        @Test
        @DisplayName("Mail не может быть пустым")
        void whenMailIsEmpty_thenThrowsIllegalArgumentException(){
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "password", "secret",
                    "mail", "",
                    "age", "120");

            assertThatThrownBy(()-> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @DisplayName("Mail должен соответствовать правилам написания почты")
        @ValueSource(strings = {
                "userexample.com",
                "user@",
                "@example.com",
                "user @example.com",
                "user@ example.com",
                "a@b@c.com",
                "user@example",
                ""
        })
        void whenInvalidMail_thenThrows(String badMail) {
            Map<String, String> fields = Map.of(
                    "name", "Alice", "password", "p", "mail", badMail, "age", "30");
            assertThatThrownBy(() -> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        //negative age
        @ParameterizedTest
        @DisplayName("Age должен соответствовать правилам возраста")
        @ValueSource(strings = {
                "abc",
                "-1",
                "0",
                "121",
                ""
        })
        void whenInvalidAge_thenThrows(String badAge) {
            Map<String, String> fields = Map.of(
                    "name", "Alice", "password", "p", "mail", "alice@example.com", "age", badAge);
            assertThatThrownBy(() -> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        //все поля должны быть обязательны
        @Test
        @DisplayName("Name обязателен")
        void whenNameIsNot_thenIllegalArgumentException() {
            Map<String, String> fields = Map.of(
                    "password", "secret",
                    "mail", "alice@example.com",
                    "age", "30");

            assertThatThrownBy(() -> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Password обязателен")
        void whenPasswordIsNot_thenIllegalArgumentException() {
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "mail", "alice@example.com",
                    "age", "30");

            assertThatThrownBy(() -> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Mail обязателен")
        void whenMailIsNot_thenIllegalArgumentException() {
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "password", "secret",
                    "age", "30");

            assertThatThrownBy(() -> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Age обязателен")
        void whenAgeIsNot_thenIllegalArgumentException() {
            Map<String, String> fields = Map.of(
                    "name", "Alice",
                    "password", "secret",
                    "mail", "alice@example.com");

            assertThatThrownBy(() -> factory.fromFields(fields))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Создание без полей невозможно")
        void whenEmptyMap_thenThrows(){
            assertThatThrownBy(() -> factory.fromFields(Map.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Тесты для Random")
    class RandomTest{
        @Test
        @DisplayName("Random не должен генерировать невалидного пользователя")
        void whenUseRandom_thenUserIsCorrect() {
            for (int i = 0; i < 100; i++) {
                User user = factory.random();
                assertThat(user.getName()).isNotBlank();
                assertThat(user.getPassword()).isNotBlank();
                assertThat(user.getMail()).matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
                assertThat(user.getAge()).isBetween(18, 120);
            }
        }

        @Test
        @DisplayName("Сгенерированный возраст от 18 до 120 лет")
        void whenUseRandom_thenUsersAgeIsCorrect() {
            UserDataFactory factory = new UserDataFactory();
            for (int i = 0; i < 100; i++) {
                assertThat(factory.random().getAge()).isBetween(18, 120);
            }
        }

        @Test
        @DisplayName("Сгенерированное имя корректно, mail - name+@example.com")
        void whenUseRandom_thenNameAndMailAreConsistent() {
            User u = factory.random();
            String id = u.getName().substring("User".length());
            assertThat(u.getMail()).isEqualTo("user" + id + "@example.com");
        }
    }
}
