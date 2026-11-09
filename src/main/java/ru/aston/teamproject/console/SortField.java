package ru.aston.teamproject.console;

import ru.aston.teamproject.user.User;

import java.util.Comparator;

public enum SortField {
    NAME("name", Comparator.comparing(User::getName)),
    PASSWORD("password", Comparator.comparing(User::getPassword)),
    MAIL("mail", Comparator.comparing(User::getMail)),
    AGE("age", Comparator.comparingInt(User::getAge));

    private final String label;
    private final Comparator<User> comparator;

    SortField(String label, Comparator<User> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String label() {
        return label;
    }

    public Comparator<User> comparator() {
        return comparator;
    }
}