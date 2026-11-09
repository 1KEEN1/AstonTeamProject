package ru.aston.teamproject.console.console;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.console.SortField;
import ru.aston.teamproject.customsort.QuickSort;
import ru.aston.teamproject.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortFieldTest {
    private static User u(String name, String pwd, String mail, int age) {
        return User.builder().name(name).password(pwd).mail(mail).age(age).build();
    }

    @Test
    @DisplayName("Компаратор NAME сортирует пользователей по имени по алфавиту")
    void whenUseNameComparator_thenSortedAlphabetically() {
        User u1 = u("Bob","x","b@x",1);
        User u2 = u("Alice","y","a@x",2);
        SimpleArrayList<User> list = new SimpleArrayList<>(2) {};
        list.add(u1);
        list.add(u2);
        QuickSort.sort(list, SortField.NAME.comparator());
        assertEquals("Alice", list.get(0).getName());
    }

    @Test
    @DisplayName("Компаратор AGE сортирует пользователей по возрасту по возрастанию")
    void whenUseAgeComparator_thenSortedByAgeAscending() {
        User u1 = u("A","p","m",30);
        User u2 = u("B","p","m",20);
        SimpleArrayList<User> list = new SimpleArrayList<>(2);
        list.add(u1);
        list.add(u2);

        QuickSort.sort(list, SortField.AGE.comparator());
        assertEquals(20, list.get(0).getAge());
    }

    @Test
    @DisplayName("Метки полей сортировки соответствуют именам: name, password, mail, age")
    void whenUseLabels_thenTheyMatch() {
        assertEquals("name",     SortField.NAME.label());
        assertEquals("password", SortField.PASSWORD.label());
        assertEquals("mail",     SortField.MAIL.label());
        assertEquals("age",      SortField.AGE.label());
    }
}
