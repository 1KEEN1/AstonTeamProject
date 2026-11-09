package ru.aston.teamproject.console;

import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.customsort.QuickSort;
import ru.aston.teamproject.service.OccurrenceCounter;
import ru.aston.teamproject.user.User;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public final class DataOperationsMenu {

    private final Scanner scanner;
    private final ConsoleIO io;
    private final CollectionPrinter printer;
    private final MenuPrinter menuPrinter = new MenuPrinter();
    private final ResultSaver saver;

    public DataOperationsMenu(Scanner scanner, ConsoleIO io, CollectionPrinter printer) {
        this.scanner = Objects.requireNonNull(scanner);
        this.io = Objects.requireNonNull(io);
        this.printer = Objects.requireNonNull(printer);
        this.saver = new ResultSaver(scanner, io);
    }

    public boolean handle(SimpleList<User> users) {
        menuPrinter.printDataMenu();
        int choice = io.readInt("Choose an action: ");
        System.out.println();

        try {
            switch (choice) {
                case 1 -> { sortAndShow(users, SortField.NAME); return true; }
                case 2 -> { sortAndShow(users, SortField.PASSWORD); return true; }
                case 3 -> { sortAndShow(users, SortField.MAIL); return true; }
                case 4 -> { sortAndShow(users, SortField.AGE); return true; }
                case 5 -> { evenAgesAndShow(users); return true; }
                case 6 -> { countOccurrences(users); return true; }
                case 0 -> { return false; }
                default -> {
                    System.out.println("Invalid menu item. Please try again.");
                    return true;
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Data error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("System error: " + e.getMessage());
        }
        return true;
    }

    private void sortAndShow(SimpleList<User> users, SortField field) throws IOException {
        SimpleArrayList<User> list = toSimpleArrayList(users);
        QuickSort.sort(list, field.comparator());
        System.out.println("--- Result of sorting by '" + field.label() + "' ---");
        printer.print(list);
        saver.offerToSave(list);
    }

    private void evenAgesAndShow(SimpleList<User> users) throws IOException {
        SimpleArrayList<User> list = toSimpleArrayList(users);
        QuickSort.sortEvenAges(list);
        System.out.println("--- Result of sorting even ages ---");
        System.out.println("(Odd ages remained in their original places)");
        printer.print(list);
        saver.offerToSave(list);
    }

    private void countOccurrences(SimpleList<User> users) throws Exception {
        int targetIndex = io.readIntInRange(
                "\nEnter the index of user N to search (0.." + (users.size() - 1) + "): ",
                0, users.size() - 1);
        int threads = io.readIntInRange(
                "Enter the number of threads (1.." + Math.max(1, users.size()) + "): ",
                1, Math.max(1, users.size()));

        User target = users.get(targetIndex);
        System.out.println("\nSearching for occurrences of: " + target);
        OccurrenceCounter.countByUser(users, target, threads);
    }

    private SimpleArrayList<User> toSimpleArrayList(SimpleList<User> list) {
        SimpleArrayList<User> result = new SimpleArrayList<>(list.size());
        for (User u : list) {
            result.add(u);
        }
        return result;
    }
}