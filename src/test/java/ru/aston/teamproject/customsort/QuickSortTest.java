package ru.aston.teamproject.customsort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.User;
import ru.aston.teamproject.collection.SimpleArrayList;

import java.time.Duration;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {
    private SimpleArrayList<User> list;
    private User u1;
    private User u2;
    private User u3;
    private User u4;
    private User u5;

    @BeforeEach
    void setUp() {
        list = new SimpleArrayList<>(10);

        // Создание пользователей через User.builder() с валидными данными
        u1 = User.builder().name("Anna").password("pass1").mail("anna@mail.com").age(25).build();
        u2 = User.builder().name("Boris").password("pass2").mail("boris@mail.com").age(18).build();
        u3 = User.builder().name("Clara").password("pass3").mail("clara@mail.com").age(32).build();
        u4 = User.builder().name("Denis").password("pass4").mail("denis@mail.com").age(2).build();
        u5 = User.builder().name("Anna").password("pass5").mail("anna2@mail.com").age(18).build();
    }

    /**
     * Проверяет, что элементы списка не нарушают порядок согласно компаратору.
     * Работает для любого T и любого Comparator — переиспользуется во всех тестах.
     */
    private static <T> void assertSorted(SimpleArrayList<T> list, Comparator<? super T> cmp) {
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(cmp.compare(list.get(i), list.get(i + 1)) <= 0,
                    "Элементы на позициях " + i + " и " + (i + 1) + " нарушают порядок");
        }
    }

    @Nested
    @DisplayName("Тестирование базового QuickSort.sort()")
    class BaseSortTests {

        @Test
        @DisplayName("Сортировка null списка не должна вызывать ошибок")
        void whenListIsNull_thenSortDoesNotThrow() {
            assertDoesNotThrow(() -> QuickSort.sort(null, Comparator.comparing(User::getName)));
        }

        @Test
        @DisplayName("Сортировка списка с одним элементов не должна вызывать ошибок")
        void whenListHasSingleElement_thenSortKeepsItUnchanged() {
            list.add(u1);
            QuickSort.sort(list, Comparator.comparing(User::getName));
            assertEquals(1, list.size());
            assertEquals(u1, list.get(0));
        }

        @Test
        @DisplayName("Сортировка с null-компаратором не должна вызывать ошибок")
        void whenComparatorIsNull_thenSortDoesNotThrow(){
            list.add(u1);
            assertDoesNotThrow(() -> QuickSort.sort(list, null));
            assertEquals(1, list.size());
            assertEquals(u1, list.get(0));
        }

        @Test
        @DisplayName("Сортировка по строковому полю (Name) по возрастанию")
        void whenSortingByName_thenListIsOrderedAlphabetically() {
            list.add(u3); // Clara
            list.add(u1); // Anna
            list.add(u2); // Boris

            QuickSort.sort(list, Comparator.comparing(User::getName));

            assertEquals(u1, list.get(0)); // Anna
            assertEquals(u2, list.get(1)); // Boris
            assertEquals(u3, list.get(2)); // Clara
        }

        @Test
        @DisplayName("Сортировка по числовому полю (Age) с дубликатами")
        void whenSortingByAgeWithDuplicates_thenListIsOrderedAscending() {
            list.add(u1); // 25
            list.add(u2); // 18
            list.add(u3); // 32
            list.add(u4); // 2
            list.add(u5); // 18

            QuickSort.sort(list, Comparator.comparingInt(User::getAge));

            assertEquals(2, list.get(0).getAge());
            assertEquals(18, list.get(1).getAge());
            assertEquals(18, list.get(2).getAge());
            assertEquals(25, list.get(3).getAge());
            assertEquals(32, list.get(4).getAge());
        }

        @Test
        @DisplayName("Сортировка по комбинации полей (Name, затем Age)")
        void whenSortingByNameThenAge_thenListIsOrderedByBothFields() {
            list.add(u5); // Anna, 18
            list.add(u1); // Anna, 25
            list.add(u2); // Boris, 18

            Comparator<User> complexId = Comparator.comparing(User::getName)
                    .thenComparingInt(User::getAge);

            QuickSort.sort(list, complexId);

            assertEquals(u5, list.get(0)); // Anna, 18 (меньший возраст идет первым)
            assertEquals(u1, list.get(1)); // Anna, 25
            assertEquals(u2, list.get(2)); // Boris, 18
        }

        @Test
        @DisplayName("Сортировка уже отсортированного списка не вызывает ошибок")
        void whenListIsAlreadySorted_thenSortKeepsItSorted(){
            list.add(u4); // 2
            list.add(u2); // 18
            list.add(u5); // 18
            list.add(u1); // 25
            list.add(u3); // 32

            Comparator<User> byAge = Comparator.comparingInt(User::getAge);

            assertDoesNotThrow(() -> QuickSort.sort(list, byAge));
            assertSorted(list, byAge);
        }

        @Test
        @DisplayName("Сортировка уже списка в обратном порядке не вызывает ошибок")
        void whenListIsReverseSorted_thenSortOrdersItAscending(){
            list.add(u3); // 32
            list.add(u1); // 25
            list.add(u5); // 18
            list.add(u2); // 18
            list.add(u4); // 2

            Comparator<User> byAge = Comparator.comparingInt(User::getAge);

            assertDoesNotThrow(() -> QuickSort.sort(list, byAge));
            assertSorted(list, byAge);
        }

        @Test
        @DisplayName("Когда все элементы одинаковые")
        void whenAllElementsHaveEqualSortKey_thenSortTerminatesAndKeepsOrder() {
            list.add(u2); // 18
            list.add(u5); // 18
            list.add(u2); // 18 (тот же объект, но это ок — проверяем, что не зациклится)
            list.add(u5); // 18

            Comparator<User> byAge = Comparator.comparingInt(User::getAge);

            assertTimeoutPreemptively(Duration.ofSeconds(2), () -> QuickSort.sort(list, byAge));
            assertSorted(list, byAge);
            assertEquals(4, list.size());
        }
    }

    @Nested
    @DisplayName("Тестирование дополнительного метода sortEvenAges()")
    class EvenAgesSortTests {
        User odd1, odd2, odd3, even1, even2, even3;
        @BeforeEach
        void initFixtures(){
            odd1 = User.builder().name("Odd1").password("p").mail("m").age(15).build();
            even1 = User.builder().name("Even1").password("p").mail("m").age(30).build();
            odd2 = User.builder().name("Odd2").password("p").mail("m").age(27).build();
            even2 = User.builder().name("Even2").password("p").mail("m").age(12).build();
            even3 = User.builder().name("Even3").password("p").mail("m").age(4).build();
            odd3 = User.builder().name("Odd3").password("p").mail("m").age(15).build();
        }

        @Test
        @DisplayName("Сортировка списка без четных элементов не должна менять исходный порядок")
        void whenListHasNoEvenAges_thenOrderIsUnchanged() {

            list.add(odd1);
            list.add(odd2);
            list.add(odd3);

            QuickSort.sortEvenAges(list);

            assertEquals(3, list.size());

            assertEquals(odd1, list.get(0));
            assertEquals(odd2, list.get(1));
            assertEquals(odd3, list.get(2));
        }

        @Test
        @DisplayName("Четные элементы сортируются, нечетные строго остаются на своих местах")
        void whenListHasMixedAges_thenEvenAgesAreSortedAndOddStayInPlace() {
            list.add(odd1);  // 15
            list.add(even1); // 30
            list.add(odd2);  // 27
            list.add(even2); // 12
            list.add(even3); // 4

            QuickSort.sortEvenAges(list);

            // Проверяем, что нечетные остались на своих исходных позициях (0 и 2)
            assertSame(odd1, list.get(0));
            assertSame(odd2, list.get(2));

            // Проверяем, что четные на индексах 1, 3, 4 отсортировались по возрастанию: 4, 12, 30
            assertSame(even3, list.get(1));
            assertSame(even2, list.get(3));
            assertSame(even1, list.get(4));
        }

        @Test
        @DisplayName("Сортировка нескольких четных элементов подряд в конце списка производится корректно")
        void whenSomeEvenElementsPlacedAtTheEndOfList_thenSortingCorrect(){
            list.add(odd1);  //15
            list.add(odd2);  //27
            list.add(even1); //30
            list.add(even2); //12
            list.add(even3); //4

            QuickSort.sortEvenAges(list);

            assertSame(odd1, list.get(0));
            assertSame(odd2, list.get(1));

            assertSame(even3, list.get(2));
            assertSame(even2, list.get(3));
            assertSame(even1, list.get(4));
        }

        @Test
        @DisplayName("Сортировка нескольких четных элементов подряд в начале списка производится корректно")
        void whenSomeEvenElementsPlacedAtTheBeginningOfList_thenSortingCorrect(){
            list.add(even1); //30
            list.add(even2); //12
            list.add(even3); //4
            list.add(odd1); //15
            list.add(odd2); //27

            QuickSort.sortEvenAges(list);

            assertSame(odd1, list.get(3));
            assertSame(odd2, list.get(4));

            assertSame(even3, list.get(0));
            assertSame(even2, list.get(1));
            assertSame(even1, list.get(2));
        }

        @Test
        @DisplayName("Корректная обработка, если в списке менее двух четных элементов")
        void whenListHasLessThanTwoEvenAges_thenListIsUnchanged() {
            SimpleArrayList<User> singleEvenList = new SimpleArrayList<>(5);
            singleEvenList.add(odd1); // 15 (нечетный)
            singleEvenList.add(even1); // 30 (единственный четный)

            QuickSort.sortEvenAges(singleEvenList);

            // Ничего не должно измениться
            assertEquals(odd1, singleEvenList.get(0));
            assertEquals(even1, singleEvenList.get(1));
            assertEquals(even1, singleEvenList.get(1));
        }

        @Test
        @DisplayName("Сортировка null списка не должна вызывать ошибок")
        void whenListIsNull_thenSortDoesNotThrow(){
            assertDoesNotThrow(() -> QuickSort.sortEvenAges(null));
        }

        @Test
        @DisplayName("Сортировка пустого списка не должна вызывать ошибок")
        void whenListIsEmpty_thenSortEvenAgesDoesNotThrow(){
            assertDoesNotThrow(() -> QuickSort.sortEvenAges(list));
            assertEquals(0, list.size());
        }
    }
}