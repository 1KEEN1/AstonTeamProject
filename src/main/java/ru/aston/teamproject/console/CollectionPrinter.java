package ru.aston.teamproject.console;

import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.user.User;

public final class CollectionPrinter {

    public void print(SimpleList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("[%2d] %s%n", i, users.get(i));
        }
    }
}