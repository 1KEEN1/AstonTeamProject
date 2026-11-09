package ru.aston.teamproject.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.input.DataInputStrategy;
import ru.aston.teamproject.input.RandomInputStrategy;
import ru.aston.teamproject.user.User;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class DataFactoryTest {
    DataInputStrategy<User> strategy = new RandomInputStrategy<>(new UserDataFactory());

    @Test
    @DisplayName("Random должен генерировать юзеров с валидными данными в нужном количестве")
    void whenUseRandomStrategyWithUserFactory_thenProducesNValidUsers(){
        SimpleList<User> users = strategy.read(10);

        assertEquals(10, users.size(), "Должно быть сгенерировано 10 пользователей");

        for (User u : users) {
            assertAll("Проверка пользователя: " + u,
                    () -> assertNotNull(u.getName(), "name не должен быть null"),
                    () -> assertFalse(u.getName().isBlank(), "name не должен быть пустым"),
                    () -> assertNotNull(u.getMail(), "mail не должен быть null"),
                    () -> assertTrue(u.getMail().contains("@"), "mail должен содержать @"),
                    () -> assertTrue(u.getAge() >= 18 && u.getAge() <= 120,
                            "age должен быть в диапазоне 18..120, а был: " + u.getAge())
            );
        }
    }

    @Test
    @DisplayName("Если передан count=0, то создается пустая коллекция")
    void whenCountIsZero_thenCollectionIsEmpty(){
        assertEquals(0, strategy.read(0).size());
    }

    @Test
    @DisplayName("Создание коллекции отрицательной длины недопустимо")
    void whenCountIsNegative_thenThrowsIllegalArgumentException(){
        assertThatThrownBy(()-> strategy.read(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}
