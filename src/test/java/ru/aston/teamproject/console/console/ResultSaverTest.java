package ru.aston.teamproject.console.console;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.console.ConsoleIO;
import ru.aston.teamproject.console.ResultSaver;
import ru.aston.teamproject.user.User;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultSaverTest {
    @TempDir
    Path tmp;

    private ByteArrayOutputStream out;
    private PrintStream oldOut;

    @BeforeEach
    void setup() {
        out = new ByteArrayOutputStream();
        oldOut = System.out;
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restore() {
        System.setOut(oldOut);
    }

    private static User u(String name, String pwd, String mail, int age) {
        return User.builder().name(name).password(pwd).mail(mail).age(age).build();
    }

    private ResultSaver saverFor(String input) {
        Scanner sc = new Scanner(new StringReader(input));
        return new ResultSaver(sc, new ConsoleIO(sc));
    }

    private SimpleArrayList<User> sample() {
        SimpleArrayList<User> l = new SimpleArrayList<>(1);
        l.add(u("Alice","p","a@x",20));
        return l;
    }

    @Test
    @DisplayName("Отказ пользователя (n) — файл не создаётся, печатается 'Save cancelled'")
    void whenUserDeclines_thenDoesNothing() {
        ResultSaver saver = saverFor("n\n");
        saver.offerToSave(sample());
        assertTrue(out.toString().contains("Save cancelled"));
    }

    @Test
    @DisplayName("Подтверждение (y) и валидный путь — файл записан и содержит данные")
    void whenConfirmedAndValidPath_thenWritesFile() throws Exception {
        Path target = tmp.resolve("out.txt");
        ResultSaver saver = saverFor("y\n" + target + "\n");
        saver.offerToSave(sample());

        assertTrue(out.toString().contains("successfully appended"));
        assertTrue(Files.exists(target));
        assertTrue(Files.readString(target).contains("Alice"));
    }

    @Test
    @DisplayName("Пустой путь — ошибка 'must not be empty', затем отмена через '0'")
    void whenRetriesOnEmptyPath_thenCancelsWithZero() {
        ResultSaver saver = saverFor("y\n\n0\n"); // пусто -> ошибка -> отмена
        saver.offerToSave(sample());
        String s = out.toString();
        assertTrue(s.contains("must not be empty"));
        assertTrue(s.contains("Save cancelled"));
    }

    @Test
    @DisplayName("Путь указывает на директорию — ошибка 'is a directory'")
    void whenDirectoryPath_thenRejects() throws Exception {
        Path dir = Files.createDirectory(tmp.resolve("dir"));
        ResultSaver saver = saverFor("y\n" + dir + "\n0\n");
        saver.offerToSave(sample());
        assertTrue(out.toString().contains("is a directory"));
    }

    @Test
    @DisplayName("Родительская директория не существует — ошибка 'does not exist'")
    void whenNonExistentParentDir_thenRejects() {
        Path bad = tmp.resolve("no-such-dir/file.txt");
        ResultSaver saver = saverFor("y\n" + bad + "\n0\n");
        saver.offerToSave(sample());
        assertTrue(out.toString().contains("does not exist"));
    }
}
