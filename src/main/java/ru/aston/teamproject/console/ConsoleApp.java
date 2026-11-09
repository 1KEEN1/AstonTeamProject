package ru.aston.teamproject.console;

import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.user.User;

import java.util.Scanner;

/**
 * Запускает основной цикл приложения: чтение данных -> обработка данных -> запрос новых данных / выход.
 */
public final class ConsoleApp {

    private final Scanner scanner;
    private final ConsoleIO io;
    private final MenuPrinter menuPrinter;
    private final CollectionPrinter printer;
    private final InputStrategySelector inputSelector;
    private final DataOperationsMenu operationsMenu;

    public ConsoleApp() {
        this(new Scanner(System.in));
    }

    ConsoleApp(Scanner scanner) {
        this.scanner = new Scanner(System.in);
        this.io = new ConsoleIO(scanner);
        this.menuPrinter = new MenuPrinter();
        this.printer = new CollectionPrinter();
        this.inputSelector = new InputStrategySelector(scanner, io);
        this.operationsMenu = new DataOperationsMenu(scanner, io, printer);
    }

    public void run() {
        menuPrinter.printWelcome();
        boolean running = true;

        while (running) {
            SimpleList<User> users = inputSelector.selectAndRead();
            if (users == null || users.size() == 0) {
                System.out.println("Collection is empty. Returning to main menu.");
                continue;
            }

            System.out.println("\n--- Initial Collection ---");
            printer.print(users);

            processData(users);

            System.out.print("Do you want to input a new data set? (y/N): ");
            String answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("y")) {
                running = false;
                System.out.println("\nShutting down. Goodbye!");
            }
            System.out.println();
        }
    }

    private void processData(SimpleList<User> users) {
        boolean processingData = true;
        while (processingData) {
            boolean processed = operationsMenu.handle(users);
            if (!processed) {
                return;
            }
            System.out.println("\n==============================================");
            System.out.print("Would you like to perform another operation with this data? (y/N): ");
            String answer = scanner.nextLine().trim();
            if (!answer.equalsIgnoreCase("y")) {
                processingData = false;
            }
            System.out.println();
        }
    }
}