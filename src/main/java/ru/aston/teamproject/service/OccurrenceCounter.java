package ru.aston.teamproject.service;

import ru.aston.teamproject.user.User;
import ru.aston.teamproject.collection.SimpleList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Многопоточный подсчёт вхождений элемента N в кастомную коллекцию.
 * Элемент N сравнивается с элементами коллекции через {@link User#equals(Object)},
 * то есть совпадение всех полей: name + password + mail + age.
 */
public final class OccurrenceCounter {

    private OccurrenceCounter() {
    }

    /**
     * Считает количество вхождений {@code target} в {@code users}
     * и печатает результат в консоль.
     *
     * @param users   коллекция для поиска (не null)
     * @param target  эталонный элемент N (не null)
     * @param threads количество потоков, будет обрезано до {@code users.size()}
     * @return количество полных совпадений
     */
    public static int countByUser(SimpleList<User> users, User target, int threads) throws InterruptedException, ExecutionException {

        Objects.requireNonNull(users, "The users collection cannot be null");
        Objects.requireNonNull(target, "The target object cannot be null");
        if (threads <= 0) {
            throw new IllegalArgumentException("The number of threads must be greater than zero, received: " + threads);
        }

        final int size = users.size();
        if (size == 0) {
            System.out.println("Number of occurrences: 0 (collection is empty)");
            return 0;
        }

        final int workerCount = Math.min(threads, size);
        final int chunk = (size + workerCount - 1) / workerCount;

        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Future<Integer>> futures = new ArrayList<>(workerCount);

            for (int t = 0; t < workerCount; t++) {
                final int from = t * chunk;
                final int to = Math.min(from + chunk, size);
                if (from >= to) break;

                futures.add(executor.submit(() -> {
                    int local = 0;
                    for (int i = from; i < to; i++) {
                        if (target.equals(users.get(i))) {
                            local++;
                        }
                    }
                    return local;
                }));
            }

            int total = 0;
            for (Future<Integer> f : futures) {
                total += f.get();   // задачи конечные, без таймаута
            }

            System.out.printf("Number of occurrences for user %s: %d (threads: %d, elements: %d)%n", target.getName(), total, workerCount, size);
            return total;

        } finally {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("The thread pool did not finish within 5 seconds");
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}