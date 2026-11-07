package ru.aston.teamproject;

import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.customsort.QuickSort;
import ru.aston.teamproject.factory.UserDataFactory;
import ru.aston.teamproject.input.DataInputStrategy;
import ru.aston.teamproject.input.JsonInputStrategy;
import ru.aston.teamproject.input.ManualInputStrategy;
import ru.aston.teamproject.input.RandomInputStrategy;
import ru.aston.teamproject.output.FileResultWriter;
import ru.aston.teamproject.service.OccurrenceCounter;
import ru.aston.teamproject.user.User;
import ru.aston.teamproject.validation.InputValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public final class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final UserDataFactory FACTORY = new UserDataFactory();
    private static final FileResultWriter<User> WRITER = new FileResultWriter<>();

    private Main() {
    }

    public static void main(String[] args) {
        printWelcome();
        boolean running = true;

        while (running) {
            // 1. Request data first
            SimpleList<User> users = createCollection();
            if (users == null || users.size() == 0) {
                System.out.println("Collection is empty. Returning to main menu.");
                continue;
            }

            // 2. Display initial collection
            System.out.println("\n--- Initial Collection ---");
            printCollection(users);

            // 3. Loop for processing the same data multiple times
            boolean processingData = true;
            while (processingData) {
                boolean processed = processDataMenu(users);

                if (!processed) {
                    break; // User chose "0. Return to data input (skip)"
                }

                // Ask to continue with the SAME data
                System.out.println("\n==============================================");
                System.out.print("Would you like to perform another operation with this data? (y/N): ");
                String answer = SCANNER.nextLine().trim();
                if (!answer.equalsIgnoreCase("y")) {
                    processingData = false;
                }
                System.out.println();
            }

            // 4. Ask whether to input new data or exit
            System.out.print("Do you want to input a new data set? (y/N): ");
            String answer = SCANNER.nextLine().trim();
            if (!answer.equalsIgnoreCase("y")) {
                running = false;
                System.out.println("\nShutting down. Goodbye!");
            }
            System.out.println();
        }
    }

    private static void printWelcome() {
        System.out.println("==============================================");
        System.out.println("        WELCOME TO THE SYSTEM!                ");
        System.out.println("==============================================");
        System.out.println("This program helps you process user data:");
        System.out.println("sort it or count occurrences.");
        System.out.println("==============================================\n");
    }

    private static boolean processDataMenu(SimpleList<User> users) {
        System.out.println("\n--- What would you like to do with this data? ---");
        System.out.println(" 1. Sort by 'name' (alphabetically)");
        System.out.println(" 2. Sort by 'password'");
        System.out.println(" 3. Sort by 'mail'");
        System.out.println(" 4. Sort by 'age' (ascending)");
        System.out.println(" 5. Extra task: sort even ages only");
        System.out.println(" 6. Count occurrences of a user (multithreaded)");
        System.out.println(" 0. Return to data input (skip)");
        System.out.println("=================================================");

        int choice = readIntSafely("Choose an action: ");
        System.out.println();

        try {
            switch (choice) {
                case 1 -> {
                    processSorting(users, SortField.NAME);
                    return true;
                }
                case 2 -> {
                    processSorting(users, SortField.PASSWORD);
                    return true;
                }
                case 3 -> {
                    processSorting(users, SortField.MAIL);
                    return true;
                }
                case 4 -> {
                    processSorting(users, SortField.AGE);
                    return true;
                }
                case 5 -> {
                    processEvenAgesSorting(users);
                    return true;
                }
                case 6 -> {
                    processOccurrenceCount(users);
                    return true;
                }
                case 0 -> {
                    return false;
                }
                default -> {
                    System.out.println("Invalid menu item. Please try again.");
                    return true; // Stay in the processing loop
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Data error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("System error: " + e.getMessage());
        }
        return true;
    }

    private static void processSorting(SimpleList<User> users, SortField field) throws IOException {
        Comparator<User> comparator = switch (field) {
            case NAME -> Comparator.comparing(User::getName);
            case PASSWORD -> Comparator.comparing(User::getPassword);
            case MAIL -> Comparator.comparing(User::getMail);
            case AGE -> Comparator.comparingInt(User::getAge);
        };

        SimpleArrayList<User> sortableList = toSimpleArrayList(users);
        QuickSort.sort(sortableList, comparator);

        System.out.println("--- Result of sorting by '" + field.label + "' ---");
        printCollection(sortableList);

        offerToSave(sortableList);
    }

    private static void processEvenAgesSorting(SimpleList<User> users) throws IOException {
        SimpleArrayList<User> sortableList = toSimpleArrayList(users);
        QuickSort.sortEvenAges(sortableList);

        System.out.println("--- Result of sorting even ages ---");
        System.out.println("(Odd ages remained in their original places)");
        printCollection(sortableList);

        offerToSave(sortableList);
    }

    private static void processOccurrenceCount(SimpleList<User> users) throws Exception {
        int targetIndex = readIntSafelyInRange(
                "\nEnter the index of user N to search (0.." + (users.size() - 1) + "): ",
                0, users.size() - 1);
        int threads = readIntSafelyInRange(
                "Enter the number of threads (1.." + Math.max(1, users.size()) + "): ",
                1, Math.max(1, users.size()));

        User target = users.get(targetIndex);
        System.out.println("\nSearching for occurrences of: " + target);

        OccurrenceCounter.countByUser(users, target, threads);
    }

    private static SimpleList<User> createCollection() {
        DataInputStrategy<User> strategy = null;

        while (strategy == null) {
            System.out.println("\n--- Input Setup ---");
            System.out.println("1. Generate randomly");
            System.out.println("2. Enter manually");
            System.out.println("3. Load from JSON file");
            System.out.println("0. Exit the program");
            System.out.println("==============================");

            int choice = readIntSafely("Choose input method: ");

            if (choice == 0) {
                System.out.println("Shutting down. Goodbye!");
                System.exit(0);
            }

            switch (choice) {
                case 1 -> strategy = new RandomInputStrategy<>(FACTORY);
                case 2 -> strategy = new ManualInputStrategy<>(SCANNER, FACTORY);
                case 3 -> {
                    while (true) {
                        System.out.print("Enter path to JSON file (e.g. users.json), or type '0' to go back: ");
                        String pathStr = SCANNER.nextLine().trim();

                        if (pathStr.equals("0")) {
                            System.out.println("Input cancelled. Returning to main menu.");
                            return null;
                        }

                        if (pathStr.isEmpty()) {
                            System.out.println("Error: path must not be empty. Please try again.");
                            continue;
                        }

                        Path path = Path.of(pathStr);
                        if (!path.toFile().exists()) {
                            System.out.println("Error: file not found at '" + pathStr + "'. Please try again.");
                            continue;
                        }

                        if (path.toFile().isDirectory()) {
                            System.out.println("Error: '" + pathStr + "' is a directory, not a file. Please provide a path to a JSON file.");
                            continue;
                        }

                        if (!path.toFile().canRead()) {
                            System.out.println("Error: file at '" + pathStr + "' is not readable. Please try again.");
                            continue;
                        }

                        try {
                            strategy = new JsonInputStrategy<>(path, FACTORY);
                        } catch (Exception e) {
                            System.out.println("Error: failed to load JSON file '" + pathStr + "': " + e.getMessage());
                            System.out.println("Please try another file.");
                            continue;
                        }
                        break;
                    }
                }
                default -> System.out.println("Invalid input method. Please try again.");
            }
        }

        int count = readIntSafely("How many elements to input? ");
        InputValidator.nonNegativeCount(count);

        System.out.println("Reading data...");
        return strategy.read(count);
    }

    private static SimpleArrayList<User> toSimpleArrayList(SimpleList<User> list) {
        SimpleArrayList<User> result = new SimpleArrayList<>(list.size());
        for (User u : list) {
            result.add(u);
        }
        return result;
    }

    private static void printCollection(SimpleList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("[%2d] %s%n", i, users.get(i));
        }
    }

    private static void offerToSave(SimpleList<User> users) throws IOException {
        System.out.print("\nSave result to file? (y/N): ");
        String answer = SCANNER.nextLine().trim();
        if (!answer.equalsIgnoreCase("y")) {
            System.out.println("Save cancelled.");
            return;
        }

        while (true) {
            System.out.print("Enter file path (e.g. result.txt), or type '0' to cancel: ");
            String pathStr = SCANNER.nextLine().trim();

            if (pathStr.equals("0")) {
                System.out.println("Save cancelled.");
                return;
            }

            if (pathStr.isEmpty()) {
                System.out.println("Error: path must not be empty. Please try again.");
                continue;
            }

            Path path = Path.of(pathStr);

            if (path.toFile().isDirectory()) {
                System.out.println("Error: '" + pathStr + "' is a directory, not a file. Please provide a path to a file (e.g. result.txt).");
                continue;
            }

            Path parent = path.toAbsolutePath().getParent();
            if (parent != null && !parent.toFile().exists()) {
                System.out.println("Error: directory '" + parent + "' does not exist. Please provide a valid path.");
                continue;
            }

            List<User> plainList = new ArrayList<>();
            for (User u : users) {
                plainList.add(u);
            }

            try {
                WRITER.append(path, plainList, User::toString);
                System.out.println("Data successfully appended to file: " + pathStr);
                return;
            } catch (IOException e) {
                System.out.println("Error: failed to write to '" + pathStr + "': " + e.getMessage());
                System.out.println("Please try another path.");
            }
        }
    }

    private static int readIntSafely(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter an integer.");
            }
        }
    }

    private static int readIntSafelyInRange(String prompt, int min, int max) {
        while (true) {
            int val = readIntSafely(prompt);
            if (val >= min && val <= max) {
                return val;
            }
            System.out.println("Error: number must be in range " + min + ".." + max);
        }
    }

    private enum SortField {
        NAME("name"),
        PASSWORD("password"),
        MAIL("mail"),
        AGE("age");

        private final String label;

        SortField(String label) {
            this.label = label;
        }
    }
}