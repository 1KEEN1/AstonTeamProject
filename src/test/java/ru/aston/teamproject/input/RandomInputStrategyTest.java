package ru.aston.teamproject.input;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.collection.SimpleList;
import ru.aston.teamproject.factory.FakeDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RandomInputStrategyTest {
    @Test
    @DisplayName("Конструктор с null-фабрикой бросает NPE")
    void whenFactoryIsNull_thenConstructorThrows(){
        assertThatThrownBy(()-> new RandomInputStrategy<String>(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("read(0) возвращает пустой список и не вызывает фабрику")
    void whenCountIsZero_thenReturnsEmptyList_andDoesNotCallFactory(){
        DataInputStrategy<String> strategy = new RandomInputStrategy<>(FakeDataFactory.exploding());

        SimpleList<String> result = strategy.read(0);

        assertThat(result.size()).isZero();
    }

    @Test
    @DisplayName("read(-1) бросает IllegalArgumentException")
    void whenCountIsNegative_thenThrows(){
        DataInputStrategy<String> strategy = new RandomInputStrategy<>(FakeDataFactory.fixed("x"));

        assertThatThrownBy(()->strategy.read(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("read(N) возвращает ровно N элементов")
    void whenCountIsPositive_thenReturnsExactlyNElements(){
        DataInputStrategy<String> strategy = new RandomInputStrategy<>(FakeDataFactory.fixed("x"));
        SimpleList<String> result = strategy.read(5);

        assertThat(result.size()).isEqualTo(5);
        assertThat(TestCollections.toList(result)).containsExactly("x", "x", "x", "x", "x");
    }

    @Test
    @DisplayName("read(N) вызывает random() ровно N раз")
    void whenCountIsPositive_thenFactoryRandomCalledExactlyNTimes() {
        FakeDataFactory.Counting<String> factory = new FakeDataFactory.Counting<>("x");
        DataInputStrategy<String> strategy = new RandomInputStrategy<>(factory);

        strategy.read(7);

        assertThat(factory.callCount()).isEqualTo(7);
    }

    @Test
    @DisplayName("Если фабрика вернула null, read() бросает NPE")
    void whenFactoryReturnsNull_thenThrowsNpe(){
        DataInputStrategy<String> strategy =
                new RandomInputStrategy<>(FakeDataFactory.fixed(null));

        assertThatThrownBy(()->strategy.read(1)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Порядок элементов сохраняется")
    void whenCountIsPositive_thenOrderIsPreserved() {
        DataInputStrategy<String> strategy =
                new RandomInputStrategy<>(FakeDataFactory.cycling("a", "b", "c"));
        SimpleList<String> result = strategy.read(5);

        assertThat(TestCollections.toList(result)).containsExactly("a", "b", "c", "a", "b");
    }

    @Test
    @DisplayName("Возвращаемый список мутабельный")
    void whenRead_thenReturnedListIsMutable() {
        DataInputStrategy<String> strategy = new RandomInputStrategy<>(FakeDataFactory.fixed("x"));

        SimpleList<String> result = strategy.read(1);
        result.add("y");

        assertThat(result.size()).isEqualTo(2);
    }
}
