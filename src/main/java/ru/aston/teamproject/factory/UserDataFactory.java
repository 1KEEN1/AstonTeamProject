package ru.aston.teamproject.factory;
import ru.aston.teamproject.validation.InputValidator;

import ru.aston.teamproject.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class UserDataFactory implements DataFactory<User> {
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String MAIL = "mail";
    private static final String AGE = "age";
    private static final int MAX_AGE = 120;

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    public List<String> fieldNames() {
        return List.of(NAME, PASSWORD, MAIL, AGE);
    }

    @Override
    public User fromFields(Map<String, String> fields) {
        String name = InputValidator.nonBlank(fields.get(NAME), NAME);
        String password = InputValidator.nonBlank(fields.get(PASSWORD), PASSWORD);
        String mail = InputValidator.nonBlank(fields.get(MAIL), MAIL);
        int age = InputValidator.positiveInt(fields.get(AGE), AGE);
        if (!EMAIL.matcher(mail).matches()) {
            throw new IllegalArgumentException("Некорректный адрес электронной почты");
        }
        if (age > MAX_AGE) {
            throw new IllegalArgumentException("Возраст не может превышать " + MAX_AGE);
        }
        return User.builder().name(name).password(password).mail(mail).age(age).build();
    }

    @Override
    public User random() {
        int id = ThreadLocalRandom.current().nextInt(1000, 100000);
        return fromFields(Map.of(NAME, "User" + id,
                PASSWORD, "DemoPassword" + id,
                MAIL, "user" + id + "@example.com",
                AGE, Integer.toString(ThreadLocalRandom.current().nextInt(18, 81))));
    }
}
