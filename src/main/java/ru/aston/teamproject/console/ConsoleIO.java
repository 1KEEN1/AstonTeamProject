package ru.aston.teamproject.console;

import java.util.Objects;
import java.util.Scanner;

public final class ConsoleIO {

    private final Scanner scanner;

    public ConsoleIO(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner);
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter an integer.");
            }
        }
    }

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.println("Error: number must be in range " + min + ".." + max);
        }
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public boolean confirm(String prompt) {
        return readLine(prompt).equalsIgnoreCase("y");
    }
}