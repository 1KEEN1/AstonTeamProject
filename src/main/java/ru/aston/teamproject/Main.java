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
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntSafely("Choose an action: ");
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> processSorting(SortField.NAME);
                    case 2 -> processSorting(SortField.PASSWORD);
                    case 3 -> processSorting(SortField.MAIL);
                    case 4 -> processSorting(SortField.AGE);
                    case 5 -> processEvenAgesSorting();
                    case 6 -> processOccurrenceCount();
                    case 0 -> {
                        running = false;
                        System.out.println("Shutting down. Goodbye!");
                    }
                    default -> System.out.println("Invalid menu item. Please try again.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Data error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("System error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("==============================================");
        System.out.println("          USER SORTING SYSTEM                 ");
        System.out.println("==============================================");
        System.out.println(" 1. Sort by 'name' (alphabetically)");
        System.out.println(" 2. Sort by 'password'");
        System.out.println(" 3. Sort by 'mail'");
        System.out.println(" 4. Sort by 'age' (ascending)");
        System.out.println(" 5. Extra task: sort even ages only");
        System.out.println(" 6. Count occurrences of a user (multithreaded)");
        System.out.println(" 0. Exit");
        System.out.println("==============================================");
    }

    private static void processSorting(SortField field) throws IOException {
        SimpleList<User> users = createCollection();
        if (users == null || users.size() == 0) return;

        System.out.println("\n--- Initial collection ---");
        printCollection(users);

        Comparator<User> comparator = switch (field) {
            case NAME -> Comparator.comparing(User::getName);
            case PASSWORD -> Comparator.comparing(User::getPassword);
            case MAIL -> Comparator.comparing(User::getMail);
            case AGE -> Comparator.comparingInt(User::getAge);
        };

        SimpleArrayList<User> sortableList = toSimpleArrayList(users);
        QuickSort.sort(sortableList, comparator);

        System.out.println("\n--- After sorting by '" + field.label + "' ---");
        printCollection(sortableList);

        offerToSave(sortableList);
    }

    private static void processEvenAgesSorting() throws IOException {
        SimpleList<User> users = createCollection();
        if (users == null || users.size() == 0) return;

        System.out.println("\n--- Initial collection ---");
        printCollection(users);

        SimpleArrayList<User> sortableList = toSimpleArrayList(users);
        QuickSort.sortEvenAges(sortableList);

        System.out.println("\n--- After sorting even ages (odd ages stayed in place) ---");
        printCollection(sortableList);

        offerToSave(sortableList);
    }

    private static void processOccurrenceCount() throws Exception {
        SimpleList<User> users = createCollection();
        if (users == null || users.size() == 0) return;

        System.out.println("\n--- Generated collection ---");
        printCollection(users);

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
        System.out.println("\n--- Input setup ---");
        System.out.println("1. Generate randomly");
        System.out.println("2. Enter manually");
        System.out.println("3. Load from JSON file");
        System.out.println("0. Cancel");

        int choice = readIntSafely("Choose input method: ");
        if (choice == 0) return null;

        DataInputStrategy<User> strategy = switch (choice) {
            case 1 -> new RandomInputStrategy<>(FACTORY);
            case 2 -> new ManualInputStrategy<>(SCANNER, FACTORY);
            case 3 -> {
                System.out.print("Enter path to JSON file (e.g. users.json): ");
                String path = SCANNER.nextLine().trim();
                if (path.isEmpty()) throw new IllegalArgumentException("Path must not be empty");
                yield new JsonInputStrategy<>(Path.of(path), FACTORY);
            }
            default -> throw new IllegalArgumentException("Invalid input method");
        };

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
        if (!answer.equalsIgnoreCase("y")) return;

        System.out.print("Enter file path (e.g. result.txt): ");
        String pathStr = SCANNER.nextLine().trim();
        if (pathStr.isEmpty()) {
            System.out.println("Empty path — save cancelled.");
            return;
        }

        List<User> plainList = new ArrayList<>();
        for (User u : users) {
            plainList.add(u);
        }

        WRITER.append(Path.of(pathStr), plainList, User::toString);
        System.out.println("Data successfully appended to file: " + pathStr);
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