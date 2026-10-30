package ru.aston.teamproject.customsort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.User;
import ru.aston.teamproject.collection.SimpleArrayList;

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

    @Nested
    @DisplayName("Тестирование базового QuickSort.sort()")
    class BaseSortTests {

        @Test
        @DisplayName("Сортировка null, пустого и единичного списков не должна вызывать ошибок")
        void testEdgeCases() {
            // Null список
            assertDoesNotThrow(() -> QuickSort.sort(null, Comparator.comparing(User::getName)));

            // Null компаратор
            list.add(u1);
            assertDoesNotThrow(() -> QuickSort.sort(list, null));

            // Список из 1 элемента
            QuickSort.sort(list, Comparator.comparing(User::getName));
            assertEquals(1, list.size());
            assertEquals(u1, list.get(0));
        }

        @Test
        @DisplayName("Сортировка по строковому полю (Name) по возрастанию")
        void testSortByName() {
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
        void testSortByAgeWithDuplicates() {
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
        void testSortByMultipleFields() {
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
    }

    @Nested
    @DisplayName("Тестирование дополнительного метода sortEvenAges()")
    class EvenAgesSortTests {

        @Test
        @DisplayName("Сортировка списка без четных элементов не должна менять исходный порядок")
        void testNoEvenAges() {
            User odd1 = User.builder().name("U1").password("p").mail("m").age(21).build();
            User odd2 = User.builder().name("U2").password("p").mail("m").age(33).build();
            User odd3 = User.builder().name("U3").password("p").mail("m").age(15).build();

            list.add(odd1);
            list.add(odd2);
            list.add(odd3);

            QuickSort.sortEvenAges(list);

            assertEquals(odd1, list.get(0));
            assertEquals(odd2, list.get(1));
            assertEquals(odd3, list.get(2));
        }

        @Test
        @DisplayName("Четные элементы сортируются, нечетные строго остаются на своих местах")
        void testSortEvenAgesMainLogic() {
            User odd1 = User.builder().name("Odd1").password("p").mail("m").age(15).build();  // Нечетный (индекс 0)
            User even1 = User.builder().name("Even1").password("p").mail("m").age(30).build(); // Четный   (индекс 1)
            User odd2 = User.builder().name("Odd2").password("p").mail("m").age(27).build();  // Нечетный (индекс 2)
            User even2 = User.builder().name("Even2").password("p").mail("m").age(12).build(); // Четный   (индекс 3)
            User even3 = User.builder().name("Even3").password("p").mail("m").age(4).build();  // Четный   (индекс 4)

            list.add(odd1);  // 15
            list.add(even1); // 30
            list.add(odd2);  // 27
            list.add(even2); // 12
            list.add(even3); // 4

            QuickSort.sortEvenAges(list);

            // Проверяем, что нечетные остались на своих исходных позициях (0 и 2)
            assertEquals(odd1, list.get(0));
            assertEquals(odd2, list.get(2));

            // Проверяем, что четные на индексах 1, 3, 4 отсортировались по возрастанию: 4, 12, 30
            assertEquals(4, list.get(1).getAge());
            assertEquals(12, list.get(3).getAge());
            assertEquals(30, list.get(4).getAge());
        }

        @Test
        @DisplayName("Корректная обработка, если в списке менее двух четных элементов")
        void testLessThanTwoEvens() {
            SimpleArrayList<User> singleEvenList = new SimpleArrayList<>(5);
            singleEvenList.add(u1); // 25 (нечетный)
            singleEvenList.add(u2); // 18 (единственный четный)

            QuickSort.sortEvenAges(singleEvenList);

            // Ничего не должно измениться
            assertEquals(u1, singleEvenList.get(0));
            assertEquals(u2, singleEvenList.get(1));
        }
    }
}