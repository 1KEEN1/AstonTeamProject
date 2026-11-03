package ru.aston.teamproject.collection;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.input.TestCollections;

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
    @DisplayName("Добавление новых элементов в коллекцию увеличивает ее размер")
    void whenAdd_thenSizeIncrease() {
        list.add(4);
        assertThat(list.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("Удаление элемента из коллекции возвращает удаляемое значение и уменьшает её размер")
    void whenRemove_thenGetValueAndSizeDecrease() {
        assertThat(list.remove(1)).isEqualTo(2);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Удаление одного элемента не меняет остальную коллекцию")
    void whenRemove_thenMustNotBeEmpty() {
        list.remove(1);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(3);
    }

    @Test
    @DisplayName("Добавление и получение элемента с корректным индексом возвращает корректное значение")
    void whenAddAndGetByCorrectIndex_thenReturnValue() {
        list.add(4);
        assertThat(list.get(3)).isEqualTo(4);
    }

    @Test
    @DisplayName("Получение элемента с индексом больше size недопустимо")
    void whenGetByIncorrectIndex_thenGetException() {
        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Get за пределами size, но в пределах capacity недопустим")
    void whenGetByIndexLessThanCapacityButGreaterThanSize_thenException() {
        SimpleList<Integer> list = new SimpleArrayList<>(10);
        list.add(5);
        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Set за пределами size, но в пределах capacity недопустим")
    void whenSetByIndexLessThanCapacityButGreaterThanSize_thenException() {
        SimpleList<Integer> list = new SimpleArrayList<>(10);
        list.add(5);
        assertThatThrownBy(() -> list.set(5, 0))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Удаление элемента с некорректным индексом недопустимо")
    void whenRemoveByIncorrectIndex_thenGetException() {
        assertThatThrownBy(() -> list.remove(5))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("При добавлении значения null, оно сохраняется")
    void whenAddNull_thenItIsStored() {
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
    @DisplayName("При удалении первого элемента, индексы сдвигаются влево")
    void whenRemoveFirst_thenShiftLeft() {
        assertThat(list.remove(0)).isEqualTo(1);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(2);
        assertThat(list.get(1)).isEqualTo(3);
    }

    @Test
    @DisplayName("При удалении последнего элемента, индексы не сдвигаются")
    void whenRemoveLast_thenNoShift() {
        assertThat(list.remove(2)).isEqualTo(3);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.get(1)).isEqualTo(2);
    }

    @Test
    @DisplayName("При нулевой capacity добавление работает корректно")
    void whenCapacityIsZero_thenAddWorks() {
        SimpleList<Integer> list = new SimpleArrayList<>(0);
        list.add(42);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo(42);
    }

    @Test
    @DisplayName("При использовании Set, возвращается старое значение, размер коллекции не меняется")
    void whenSet_thenGetOldValueAndSizeNotChanged() {
        assertThat(list.set(1, 22)).isEqualTo(2);
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Отрицательная capacity недопустима")
    void whenNegativeCapacity_thenException() {
        assertThatThrownBy(() -> new SimpleArrayList<Integer>(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Set в некорректным индексом недопустим")
    void whenSetByIncorrectIndex_thenGetException() {
        assertThatThrownBy(() -> list.set(5, 22))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("При удалении итератор работает корректно и не пропускает значения")
    void whenRemove_thenIteratorDoesNotSkipOrThrow() {
        list.remove(0);              // size = 2, modCount = 4
        Iterator<Integer> it = list.iterator();
        assertThat(it.next()).isEqualTo(2);
        assertThat(it.next()).isEqualTo(3);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    @DisplayName("В пустом листе у элементов нет следующего значения")
    void whenGetIteratorFromEmptyList_thenHasNextReturnFalse() {
        list = new SimpleArrayList<>(5);
        assertThat(list.iterator().hasNext()).isFalse();
    }

    @Test
    @DisplayName("Попытка получить итератор в пустом листе вызовет ошибку")
    void whenGetIteratorFromEmptyList_thenNextThrowException() {
        list = new SimpleArrayList<>(5);
        assertThatThrownBy(list.iterator()::next)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("После удаления всех элементов коллекции она становится пустой и может быть переиспользована")
    void whenRemoveAll_thenListIsEmptyAndCanBeReused() {
        list.remove(0);
        list.remove(0);
        list.remove(0);
        assertThat(list.size()).isZero();
        list.add(99);
        assertThat(TestCollections.toList(list)).containsExactly(99);
    }

    @Test
    @DisplayName("Если вызвать итератор дважды, он будет начинать сначала")
    void whenGetIteratorTwice_thenStartAlwaysFromBeginning() {
        assertThat(list.iterator().next()).isEqualTo(1);
        assertThat(list.iterator().next()).isEqualTo(1);
    }

    @Test
    @DisplayName("Итератор проходит по всем элементам коллекции корректно")
    void whenCheckIterator_thenTraversesAllElementsInOrder() {
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
    @DisplayName("Коллекция динамически расширяется, при этом сохраняет все прежние элементы")
    void whenNoPlace_thenMustIncreaseCapacityAndKeepElements() {
        IntStream.range(4, 10).forEach(list::add);
        assertThat(list.size()).isEqualTo(9);
        assertThat(TestCollections.toList(list)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    @DisplayName("При добавлении элемента после получения итератора — ConcurrentModificationException")
    void whenAddAfterGetIterator_thenMustBeException() {
        Iterator<Integer> iterator = list.iterator();
        list.add(4);
        assertThatThrownBy(iterator::next)
                .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    @DisplayName("При удалении элемента после получения итератора — ConcurrentModificationException")
    void whenRemoveAfterGetIterator_thenMustBeException() {
        Iterator<Integer> iterator = list.iterator();
        list.remove(0);
        assertThatThrownBy(iterator::next)
                .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    @DisplayName("При set после получения итератора CME не бросается, итератор видит новое значение")
    void whenSetAfterGetIterator_thenNoCME() {
        Iterator<Integer> it = list.iterator();
        it.next();                    // 1
        list.set(1, 22);              // не структурная модификация
        assertThat(it.next()).isEqualTo(22);
        assertThat(it.next()).isEqualTo(3);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    @DisplayName("При вызове next() после последнего элемента — NoSuchElementException")
    void whenNextAfterLast_thenException() {
        Iterator<Integer> it = list.iterator();
        it.next(); it.next(); it.next();
        assertThatThrownBy(it::next)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Обращение к следующему элементу коллекции не вызывает ошибок")
    void whenAddAfterGetIterator_thenHasNextDoesNotThrow() {
        Iterator<Integer> it = list.iterator();
        list.add(4);
        assertThatCode(it::hasNext).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("При добавлении null в коллекцию, get возвращает null")
    void whenSetNull_thenGetReturnsNull() {
        assertThat(list.set(1, null)).isEqualTo(2);
        assertThat(list.get(1)).isNull();
    }

    @Test
    @DisplayName("Удаление null элемента не ломает логику")
    void whenRemoveNullElement_thenWorks(){
        list = new SimpleArrayList<>(3);
        list.add(null);
        list.add(1);
        assertThat(list.remove(0)).isNull();
        assertThat(list.get(0)).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Удаление элемента с отрицательным индексом недопустимо")
    void whenTryToRemoveElementWithNegativeIndex_thenThrowsIndexOfBoundsException(){
        assertThatThrownBy(()->list.remove(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Get элемента с отрицательным индексом недопустимо")
    void whenTryToGetElementWithNegativeIndex_thenThrowsIndexOfBoundsException(){
        assertThatThrownBy(()->list.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Set элемента с отрицательным индексом недопустимо")
    void whenTryToSetElementWithNegativeIndex_thenThrowsIndexOfBoundsException(){
        assertThatThrownBy(()->list.set(-1, 22))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("Размер пустой коллекции равен нулю")
    void whenEmptyList_thenSizeIsZero() {
        assertThat(new SimpleArrayList<Integer>(10).size()).isZero();
    }

    @Test
    @DisplayName("После полного прохода коллекции многократный вызов итератора будет False")
    void whenIteratorExhaustedThenHasNextStaysFalse() {
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) it.next();
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
    }
}