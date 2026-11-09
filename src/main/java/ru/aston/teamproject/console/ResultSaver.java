package ru.aston.teamproject.console;

import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.output.FileResultWriter;
import ru.aston.teamproject.user.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public final class ResultSaver {

    private final Scanner scanner;
    private final ConsoleIO io;
    private final FileResultWriter<User> writer = new FileResultWriter<>();

    public ResultSaver(Scanner scanner, ConsoleIO io) {
        this.scanner = Objects.requireNonNull(scanner);
        this.io = Objects.requireNonNull(io);
    }

    public void offerToSave(SimpleList<User> users) {
        if (!io.confirm("\nSave result to file? (y/N): ")) {
            System.out.println("Save cancelled.");
            return;
        }

        while (true) {
            String pathStr = io.readLine("Enter file path (e.g. result.txt), or type '0' to cancel: ");
            if (pathStr.equals("0")) {
                System.out.println("Save cancelled.");
                return;
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

            if (path.toFile().isDirectory()) {
                System.out.println("Error: '" + pathStr + "' is a directory, not a file.");
                continue;
            }

            Path parent = path.toAbsolutePath().getParent();
            if (parent != null && !parent.toFile().exists()) {
                System.out.println("Error: directory '" + parent + "' does not exist.");
                continue;
            }

            List<User> plain = new ArrayList<>(users.size());
            for (User u : users) {
                plain.add(u);
            }

            try {
                writer.append(path, plain, User::toString);
                System.out.println("Data successfully appended to file: " + pathStr);
                return;
            } catch (IOException e) {
                System.out.println("Error: failed to write to '" + pathStr + "': " + e.getMessage());
                System.out.println("Please try another path.");
            }
        }
    }
}