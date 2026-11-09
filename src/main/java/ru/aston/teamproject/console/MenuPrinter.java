package ru.aston.teamproject.console;

public final class MenuPrinter {

    public void printWelcome() {
        System.out.println("==============================================");
        System.out.println("                   WELCOME!                   ");
        System.out.println("==============================================");
        System.out.println("This program helps you process user data:");
        System.out.println("sort it or count occurrences.");
        System.out.println("==============================================\n");
    }

    public void printDataMenu() {
        System.out.println("\n--- What would you like to do with this data? ---");
        System.out.println(" 1. Sort by 'name' (alphabetically)");
        System.out.println(" 2. Sort by 'password'");
        System.out.println(" 3. Sort by 'mail'");
        System.out.println(" 4. Sort by 'age' (ascending)");
        System.out.println(" 5. Extra task: sort even ages only");
        System.out.println(" 6. Count occurrences of a user (multithreaded)");
        System.out.println(" 0. Return to data input (skip)");
        System.out.println("=================================================");
    }

    public void printInputMenu() {
        System.out.println("\n--- Input Setup ---");
        System.out.println("1. Generate randomly");
        System.out.println("2. Enter manually");
        System.out.println("3. Load from JSON file");
        System.out.println("0. Exit the program");
        System.out.println("==============================");
    }
}