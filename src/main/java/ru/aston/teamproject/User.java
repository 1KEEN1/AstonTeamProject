package ru.aston.teamproject;

import java.util.Objects;

public class User {
    private final String name;
    private final String password;
    private final String mail;
    private final int age;

    private User(UserBuilder userBuilder) {
        this.name = checkIfNonBlank(userBuilder.name, "name");
        this.password = checkIfNonBlank(userBuilder.password, "password");
        this.mail = checkIfNonBlank(userBuilder.mail, "mail");
        this.age = checkAge(userBuilder.age);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", age=" + age +
                '}';
    }

    private static String checkIfNonBlank(String value, String field) {
        Objects.requireNonNull(value, field + "is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    private static int checkAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        return age;
    }

    public static class UserBuilder {
        private String name;
        private String password;
        private String mail;
        private int age;

        public UserBuilder() {
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder mail(String mail) {
            this.mail = mail;
            return this;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
