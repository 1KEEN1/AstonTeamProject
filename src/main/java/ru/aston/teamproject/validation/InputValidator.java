package ru.aston.teamproject.validation;

public final class InputValidator {
    private InputValidator() {
    }

    public static String nonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Field '" + field + "' can't be empty");
        }
        return value.trim();
    }

    public static int positiveInt(String value, String field) {
        String text = nonBlank(value, field);
        try {
            int number = Integer.parseInt(text);
            if (number <= 0) {
                throw new IllegalArgumentException("Field '" + field + "' need to be more than 0");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field '" + field + "' must be an integer", e);
        }
    }

    public static void nonNegativeCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The number of elements cannot be negative");
        }
    }


    public static void validateAvailableSpace(int requestedCount, int availableSpace) {
        nonNegativeCount(requestedCount);

        if (availableSpace < 0) {
            throw new IllegalStateException(
                    "Incorrect number of available spots"
            );
        }

        if (requestedCount > availableSpace) {
            throw new IllegalArgumentException(
                    "Not enough space. Available: "
                            + availableSpace
            );
        }
    }
}