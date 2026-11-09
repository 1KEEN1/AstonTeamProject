package ru.aston.teamproject.console;

import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.UserDataFactory;
import ru.aston.teamproject.input.DataInputStrategy;
import ru.aston.teamproject.input.JsonInputStrategy;
import ru.aston.teamproject.input.ManualInputStrategy;
import ru.aston.teamproject.input.RandomInputStrategy;
import ru.aston.teamproject.user.User;
import ru.aston.teamproject.validation.InputValidator;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

public final class InputStrategySelector {

    private final Scanner scanner;
    private final ConsoleIO io;
    private final UserDataFactory factory = new UserDataFactory();
    private final MenuPrinter menuPrinter = new MenuPrinter();

    public InputStrategySelector(Scanner scanner, ConsoleIO io) {
        this.scanner = Objects.requireNonNull(scanner);
        this.io = Objects.requireNonNull(io);
    }

    public SimpleList<User> selectAndRead() {
        DataInputStrategy<User> strategy = null;
        while (strategy == null) {
            menuPrinter.printInputMenu();
            int choice = io.readInt("Choose input method: ");

            if (choice == 0) {
                System.out.println("Shutting down. Goodbye!");
                System.exit(0);
            }

            switch (choice) {
                case 1 -> strategy = new RandomInputStrategy<>(factory);
                case 2 -> strategy = new ManualInputStrategy<>(scanner, factory);
                case 3 -> strategy = selectJsonStrategy();
                default -> System.out.println("Invalid input method. Please try again.");
            }
            if (strategy == null && choice == 3) {
                return null; // user cancelled JSON selection
            }
        }

        int count = io.readInt("How many elements to input? ");
        InputValidator.nonNegativeCount(count);
        System.out.println("Reading data...");
        return strategy.read(count);
    }

    private DataInputStrategy<User> selectJsonStrategy() {
        while (true) {
            String pathStr = io.readLine("Enter path to JSON file (e.g. users.json), or type '0' to go back: ");
            if (pathStr.equals("0")) {
                System.out.println("Input cancelled. Returning to main menu.");
                return null;
            }
            if (pathStr.isEmpty()) {
                System.out.println("Error: path must not be empty. Please try again.");
                continue;
            }

            Path path;
            try {
                path = Path.of(pathStr);
            } catch (Exception e) {
                System.out.println("Error: '" + pathStr + "' is not a valid path. Please try again.");
                continue;
            }

            if (!path.toFile().exists()) {
                System.out.println("Error: file not found at '" + pathStr + "'. Please try again.");
                continue;
            }
            if (path.toFile().isDirectory()) {
                System.out.println("Error: '" + pathStr + "' is a directory, not a file.");
                continue;
            }
            if (!path.toFile().canRead()) {
                System.out.println("Error: file at '" + pathStr + "' is not readable.");
                continue;
            }
            return new JsonInputStrategy<>(path, factory);
        }
    }
}