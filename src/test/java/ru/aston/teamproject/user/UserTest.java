package ru.aston.teamproject.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Nested
    class ValidationTests {
        @Test
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
        void whenUserCompareWithNull_thenFalse(){
            assertNotEquals(null, user1);
        }

        @Test
        void whenUserCompareWithOtherType_thenFalse() {
            assertNotEquals("Some string", user1);
        }
    }


}
