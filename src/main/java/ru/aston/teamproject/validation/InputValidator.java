package ru.aston.teamproject.validation;

public final class InputValidator {
    private InputValidator() { }

    public static String nonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле '" + field + "' не может быть пустым");
        }
        return value.trim();
    }

    public static int positiveInt(String value, String field) {
        String text = nonBlank(value, field);
        try {
            int number = Integer.parseInt(text);
            if (number <= 0) {
                throw new IllegalArgumentException("Поле '" + field + "' должно быть больше нуля");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле '" + field + "' должно быть целым числом", e);
        }
    }

    public static void nonNegativeCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Количество элементов не может быть отрицательным");
        }
    }

    public static void validateAvailableSpace(
            int requestedCount,
            int availableSpace
    ) {
        nonNegativeCount(requestedCount);

        if (availableSpace < 0) {
            throw new IllegalStateException(
                    "Некорректное количество свободных мест"
            );
        }

        if (requestedCount > availableSpace) {
            throw new IllegalArgumentException(
                    "Недостаточно места. Свободно: "
                            + availableSpace
            );
        }
    }
}