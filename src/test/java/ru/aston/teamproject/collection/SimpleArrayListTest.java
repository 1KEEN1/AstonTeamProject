package ru.aston.teamproject.collection;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.stream.IntStream;

class SimpleArrayListTest {

    private SimpleList<Integer> list;

    @BeforeEach
    public void initData() {
        list = new SimpleArrayList<>(3);
        list.add(1);
        list.add(2);
        list.add(3);
    }

    @Test
    void whenAddThenSizeIncrease() {
        list.add(4);
        assertThat(list.size()).isEqualTo(4);
    }

    @Test
    void whenRemoveThenGetValueAndSizeDecrease() {
        assertThat(list.remove(1)).isEqualTo(2);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void whenRemoveThenMustNotBeEmpty() {
        list.remove(1);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(3);
    }

    @Test
    void whenAddAndGetByCorrectIndex() {
        list.add(4);
        assertThat(list.get(3)).isEqualTo(4);
    }

    @Test
    void whenGetByIncorrectIndexThenGetException() {
        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void whenGetByIndexLessThanCapacityButGreaterThanSizeThenException() {
        SimpleList<Integer> list = new SimpleArrayList<>(10);
        list.add(5);
        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void whenRemoveByIncorrectIndexThenGetException() {
        assertThatThrownBy(() -> list.remove(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void whenAddNullThenItIsStored() {
        list = new SimpleArrayList<>(3);
        list.add(null);
        list.add(null);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isNull();
        assertThat(list.get(1)).isNull();
        Iterator<Integer> it = list.iterator();
        assertThat(it.next()).isNull();
        assertThat(it.next()).isNull();
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    void whenRemoveFirstThenShiftLeft() {
        assertThat(list.remove(0)).isEqualTo(1);
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isEqualTo(2);
        assertThat(list.get(1)).isEqualTo(3);
    }

    @Test
    void whenRemoveLastThenNoShift() {
        assertThat(list.remove(2)).isEqualTo(3);
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(2);
    }

    @Test
    void whenCapacityIsZeroThenAddWorks() {
        SimpleList<Integer> list = new SimpleArrayList<>(0);
        list.add(42);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(42);
    }

    @Test
    void whenSetThenGetOldValueAndSizeNotChanged() {
        assertThat(list.set(1, 22)).isEqualTo(2);
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    void whenNegativeCapacityThenException() {
        assertThatThrownBy(() -> new SimpleArrayList<Integer>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenSetByIncorrectIndexThenGetException() {
        assertThatThrownBy(() -> list.set(5, 22))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void whenRemoveThenIteratorDoesNotSkipOrThrow() {
        list.remove(0);              // size = 2, modCount = 4
        Iterator<Integer> it = list.iterator();
        assertThat(it.next()).isEqualTo(2);
        assertThat(it.next()).isEqualTo(3);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    void whenGetIteratorFromEmptyListThenHasNextReturnFalse() {
        list = new SimpleArrayList<>(5);
        assertThat(list.iterator().hasNext()).isFalse();
    }

    @Test
    void whenGetIteratorFromEmptyListThenNextThrowException() {
        list = new SimpleArrayList<>(5);
        assertThatThrownBy(list.iterator()::next)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void whenRemoveAllThenListIsEmptyAndCanBeReused() {
        list.remove(0);
        list.remove(0);
        list.remove(0);
        assertThat(list).isEmpty();
        list.add(99);
        assertThat(list).containsExactly(99);
    }

    @Test
    void whenGetIteratorTwiceThenStartAlwaysFromBeginning() {
        assertThat(list.iterator().next()).isEqualTo(1);
        assertThat(list.iterator().next()).isEqualTo(1);
    }

    @Test
    void whenCheckIterator() {
        Iterator<Integer> iterator = list.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(1);
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(2);
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(3);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void whenNoPlaceThenMustIncreaseCapacityAndKeepElements() {
        IntStream.range(4, 10).forEach(list::add);
        assertThat(list.size()).isEqualTo(9);
        assertThat(list).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    void whenAddAfterGetIteratorThenMustBeException() {
        Iterator<Integer> iterator = list.iterator();
        list.add(4);
        assertThatThrownBy(iterator::next)
                .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    void whenRemoveAfterGetIteratorThenMustBeException() {
        Iterator<Integer> iterator = list.iterator();
        list.remove(0);
        assertThatThrownBy(iterator::next)
                .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    void whenSetAfterGetIteratorThenNoCME() {
        Iterator<Integer> it = list.iterator();
        it.next();                    // 1
        list.set(1, 22);              // не структурная модификация
        assertThat(it.next()).isEqualTo(22);
        assertThat(it.next()).isEqualTo(3);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    void whenNextAfterLastThenException() {
        Iterator<Integer> it = list.iterator();
        it.next(); it.next(); it.next();
        assertThatThrownBy(it::next)
                .isInstanceOf(NoSuchElementException.class);
    }
}