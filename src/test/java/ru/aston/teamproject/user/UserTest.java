package ru.aston.teamproject.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Nested
    class ValidationTests {
        @Test
        @DisplayName("Создание пользователя с null в name невозможно")
        void whenCreateUserWithNullName_thenThrowNPE() {
            assertThrows(NullPointerException.class,
                    () -> User.builder()
                            .name(null)
                            .password("12345")
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с null в password невозможно")
        void whenCreateUserWithNullPassword_thenThrowNPE() {
            assertThrows(NullPointerException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password(null)
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с null в mail невозможно")
        void whenCreateUserWithNullMail_thenThrowNPE() {
            assertThrows(NullPointerException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password("12345")
                            .mail(null)
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с пустым name невозможно")
        void whenCreateUserWithEmptyName_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("")
                            .password("12345")
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с пробелом в name невозможно")
        void whenCreateUserWithBlankName_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name(" ")
                            .password("12345")
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с пустым password невозможно")
        void whenCreateUserWithEmptyPassword_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password("")
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с пробелом в password невозможно")
        void whenCreateUserWithBlankPassword_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password(" ")
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с пустым mail невозможно")
        void whenCreateUserWithEmptyMail_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password("12345")
                            .mail("")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с пробелом в mail невозможно")
        void whenCreateUserWithBlankMail_thenThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password("12345")
                            .mail(" ")
                            .age(18)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с 0 age невозможно")
        void whenCreateUserWithZeroAge_thenThrowIllegalArgumentException(){
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password("12345")
                            .mail("anna@mail.ru")
                            .age(0)
                            .build());
        }

        @Test
        @DisplayName("Создание пользователя с отрицательным age невозможно")
        void whenCreateUserWithNegativeAge_thenThrowIllegalArgumentException(){
            assertThrows(IllegalArgumentException.class,
                    () -> User.builder()
                            .name("Anna")
                            .password("12345")
                            .mail("anna@mail.ru")
                            .age(-20)
                            .build());
        }
    }

    @Nested
    class BuilderTests{
        @Test
        @DisplayName("Создание пользователя с валидными данными не должно вызывать ошибок")
        void whenCreateUserWithValidData_thenUserIsCreated() {
            String name = "Anna";
            String password = "12345";
            String mail = "anna@mail.ru";
            int age = 18;

            User user = User.builder()
                    .name(name)
                    .password(password)
                    .mail(mail)
                    .age(age)
                    .build();

            assertNotNull(user);
            assertEquals(name, user.getName());
            assertEquals(password, user.getPassword());
            assertEquals(mail, user.getMail());
            assertEquals(age, user.getAge());
        }

        @Test
        @DisplayName("Создание пользователя без имени невозможно")
        void whenCreateUserWithoutName_thenUserNotCreated(){
            NullPointerException exception = assertThrows(NullPointerException.class,
                    ()-> User.builder()
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build());

            assertTrue(exception.getMessage().contains("name"));
        }

        @Test
        @DisplayName("Создание пользователя без пароля невозможно")
        void whenCreateUserWithoutPassword_thenUserNotCreated(){
            NullPointerException exception = assertThrows(NullPointerException.class,
                    ()-> User.builder()
                            .name("Anna")
                            .mail("anna@mail.ru")
                            .age(18)
                            .build());

            assertTrue(exception.getMessage().contains("password"));
        }

        @Test
        @DisplayName("Создание пользователя без электронного адреса невозможно")
        void whenCreateUserWithoutMail_thenUserNotCreated(){
            NullPointerException exception = assertThrows(NullPointerException.class,
                    ()-> User.builder()
                            .name("Anna")
                            .password("12345")
                            .age(18)
                            .build());

            assertTrue(exception.getMessage().contains("mail"));
        }

        @Test
        @DisplayName("Создание пользователя без возраста невозможно")
        void whenCreateUserWithoutAge_thenUserNotCreated(){
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    ()-> User.builder()
                            .name("Anna")
                            .password("12345")
                            .mail("anna@mail.ru")
                            .build());

            assertTrue(exception.getMessage().toLowerCase().contains("age"));
        }

        @Test
        @DisplayName("Если пользователь был создан, у него можно вызвать toString")
        void whenUserIsCreated_thenToStringContainsAllFields() {
            User user = User.builder().name("Anna").password("12345").mail("anna@mail.ru").age(18).build();

            String result = user.toString();
            assertTrue(result.contains("Anna"));
            assertTrue(result.contains("12345"));
            assertTrue(result.contains("anna@mail.ru"));
            assertTrue(result.contains("18"));
        }
    }

    @Nested
    class EqualsTests{
        private User user1;

        @BeforeEach
        void setUp(){
            user1 = User.builder()
                    .name("Anna")
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build();
        }
        @Test
        @DisplayName("Два объекта класса User с одинаковыми полями должны быть равны")
        void whenUsersWithTheSameData_thenTheyEquals(){
            User user2 = User.builder()
                    .name("Anna")
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build();

            assertEquals(user1, user2);
        }

        @Test
        @DisplayName("Два объекта класса User с разными полями name не должны быть равны")
        void whenUsersHaveDifferentNames_thenTheyNotEquals(){
            User user2 = User.builder()
                    .name("Nikolay")
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build();

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Два объекта класса User с разными полями password не должны быть равны")
        void whenUsersHaveDifferentPasswords_thenTheyNotEquals(){
            User user2 = User.builder()
                    .name("Anna")
                    .password("54321")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build();

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Два объекта класса User с разными полями mail не должны быть равны")
        void whenUsersHaveDifferentMail_thenTheyNotEquals(){
            User user2 = User.builder()
                    .name("Anna")
                    .password("12345")
                    .mail("anna@gmail.com")
                    .age(18)
                    .build();

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Два объекта класса User с разными полями age не должны быть равны")
        void whenUsersHaveDifferentAge_thenTheyNotEquals(){
            User user2 = User.builder()
                    .name("Anna")
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(19)
                    .build();

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Два одинаковых объекта класса User имеют одинаковые HashCode")
        void whenUsersEqual_thenTheyHaveSameHashCode(){
            User user2 = User.builder()
                    .name("Anna")
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build();

            assertEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Два разных объекта класса User имеют разные HashCode")
        void whenUsersNotEqual_thenTheyHaveDifferentHashCode(){
            User user2 = User.builder()
                    .name("Nikolay")
                    .password("12345")
                    .mail("anna@mail.ru")
                    .age(18)
                    .build();

            assertNotEquals(user1.hashCode(), user2.hashCode());
        }

        @Test
        @DisplayName("Сравнение с Null должно возвращать false")
        void whenUserCompareWithNull_thenFalse(){
            assertNotEquals(null, user1);
        }

        @Test
        @DisplayName("Сравнение с другими типами должно возвращать false")
        void whenUserCompareWithOtherType_thenFalse() {
            assertNotEquals("Some string", user1);
        }
    }


}
