package ru.aston.teamproject.console.console;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.console.CollectionPrinter;
import ru.aston.teamproject.user.User;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionPrinterTest {
    private static User u(String name, String pwd, String mail, int age) {
        return User.builder().name(name).password(pwd).mail(mail).age(age).build();
    }

    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    void setup() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restore() {
        System.setOut(originalOut);
    }

    @Test
    void whenPrintList_thenPrintsIndexedLines() {
        SimpleArrayList<User> list = new SimpleArrayList<>(2);
        list.add(u("Alice","p","a@x",20));
        list.add(u("Bob","p","b@x",30));

        System.out.println(">>> System.out is " + System.out.getClass());
        new CollectionPrinter().print(list);
        System.out.flush();

        String s = out.toString();

        assertTrue(s.contains("[ 0]"));
        assertTrue(s.contains("[ 1]"));
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("Bob"));
    }
}
