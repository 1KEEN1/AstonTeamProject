package ru.aston.teamproject.console.console;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.console.MenuPrinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MenuPrinterTest {
    private final MenuPrinter mp = new MenuPrinter();
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final PrintStream old = System.out;

    @BeforeEach
    void setup() {
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restore() {
        System.setOut(old);
    }

    @Test
    @DisplayName("Вначале должен быть Welcome")
    void whenProgramStart_thenWelcome() {
        mp.printWelcome();
        String s = out.toString();
        assertTrue(s.contains("WELCOME"));
        assertTrue(s.contains("sort it or count occurrences"));
    }

    @Test
    @DisplayName("Главное меню должно содержать 6 пунктов")
    void whenAppearsMainMenu_thenItContainsAllPoint() {
        mp.printDataMenu();
        String s = out.toString();
        for (int i = 0; i <= 6; i++) assertTrue(s.contains(i + "."));
        assertTrue(s.contains("sort even ages"));
        assertTrue(s.contains("Count occurrences"));
    }

    @Test
    @DisplayName("Меню создания списка должно содержать 4 пункта")
    void whenAppearsInputMenu_thenItContainsAllPoint() {
        mp.printInputMenu();
        String s = out.toString();
        assertTrue(s.contains("1. Generate randomly"));
        assertTrue(s.contains("2. Enter manually"));
        assertTrue(s.contains("3. Load from JSON file"));
        assertTrue(s.contains("0. Exit"));
    }
}
