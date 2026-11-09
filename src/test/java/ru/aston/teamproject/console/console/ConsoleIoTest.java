package ru.aston.teamproject.console.console;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.console.ConsoleIO;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleIoTest {
    private ByteArrayOutputStream out;
    private PrintStream originalOut;

    @BeforeEach
    void redirectOut() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restoreOut(){
        System.setOut(originalOut);
    }

    //Подсовываем сканер над строкой, ловим вывод
    private ConsoleIO ioWith(String input){
        return new ConsoleIO(new Scanner(new StringReader(input)));
    }

    @Test
    @DisplayName("Вводиться должно число")
    void whenInputNoInt_thenAskInt(){
        ConsoleIO io = ioWith("abc\n7\n");
        assertEquals(7, io.readInt("n: "));
        assertTrue(out.toString().contains("integer"));
    }

    @Test
    @DisplayName("Вводиться должно число")
    void whenInputInt_thenParsedValue(){
        assertEquals(42, ioWith("42\n").readInt("n: "));
    }

    @Test
    @DisplayName("Лишние пробелы в начале и конце строки обрезаются")
    void whenStrContainsSpace_thenTrims(){
        assertEquals("hello", ioWith(" hello \n").readLine("x: "));
    }

    @Test
    @DisplayName("Проверка работы ввода y и n")
    void whenYesAndNo_thenConfirm(){
        assertTrue(ioWith("y\n").confirm("?"));
        assertTrue(ioWith("Y\n").confirm("?"));
        assertFalse(ioWith("n\n").confirm("?"));
        assertFalse(ioWith("\n").confirm("?"));
    }

}
