package ru.aston.teamproject.factory;
import ru.aston.teamproject.validation.InputValidator;

import ru.aston.teamproject.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class UserDataFactory implements DataFactory<User> {
    @Override
    public List<String> fieldNames() {
        return List.of("name", "password", "mail", "age");
    }

    @Override
    public User fromFields(Map<String, String> fields) {
        String name = InputValidator.nonBlank(fields.get("name"), "name");
        String password = InputValidator.nonBlank(fields.get("password"), "password");
        String mail = InputValidator.nonBlank(fields.get("mail"), "mail");
        int age = InputValidator.positiveInt(fields.get("age"), "age");
        if (!mail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Некорректный адрес электронной почты");
        }
        if (age > 120) {
            throw new IllegalArgumentException("Возраст не может превышать 120");
        }
        return User.builder().name(name).password(password).mail(mail).age(age).build();
    }

    @Override
    public User random() {
        int id = ThreadLocalRandom.current().nextInt(1000, 100000);
        return fromFields(Map.of("name", "User" + id,
                "password", "DemoPassword" + id,
                "mail", "user" + id + "@example.com",
                "age", Integer.toString(ThreadLocalRandom.current().nextInt(18, 81))));
    }
}
