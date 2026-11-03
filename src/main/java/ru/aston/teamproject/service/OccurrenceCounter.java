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

        Objects.requireNonNull(users, "Коллекция users не может быть null");
        Objects.requireNonNull(target, "Объект target не может быть null");
        if (threads <= 0) {
            throw new IllegalArgumentException("Количество потоков должно быть больше нуля, получено: " + threads);
        }

        final int size = users.size();
        if (size == 0) {
            System.out.println("Количество вхождений: 0 (коллекция пуста)");
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

            System.out.printf("Количество вхождений пользователя %s: %d (потоков: %d, элементов: %d)%n", target.getName(), total, workerCount, size);
            return total;

        } finally {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Пул потоков не завершился за 5 секунд");
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}