package ru.aston.teamproject.customsort;

import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.User;

import java.util.Comparator;

/**
 * Ручная реализация алгоритма быстрой сортировки (Quicksort) для коллекции SimpleArrayList.
 */

/**
 * В Main можно запустить например так
 * 1. Сортировка по полю 'name' (по алфавиту)
 * SimpleArrayList<User> users = createInitialList();
 * QuickSort.sort(users, (u1, u2) -> u1.getName().compareTo(u2.getName()));
 * <p>
 * 2. Сортировка по полю 'password'
 * users = createInitialList();
 * QuickSort.sort(users, (u1, u2) -> u1.getPassword().compareTo(u2.getPassword()));
 * <p>
 * 3. Сортировка по полю 'mail'
 * users = createInitialList();
 * QuickSort.sort(users, (u1, u2) -> u1.getMail().compareTo(u2.getMail()));
 * <p>
 * 4. Сортировка по полю 'age' (все элементы по возрастанию)
 * users = createInitialList();
 * QuickSort.sort(users, (u1, u2) -> Integer.compare(u1.getAge(), u2.getAge()));
 * <p>
 * 5. Выполнение дополнительного задания (только четные по возрастанию, нечетные на месте)
 * users = createInitialList();
 * QuickSort.sortEvenAges(users);
 * <p>
 * private static SimpleArrayList<User> createInitialList() {
 * SimpleArrayList<User> list = new SimpleArrayList<>(10);
 * <p>
 * list.add(User.builder().name("Denis").password("qwerty").mail("denis@mail.com").age(12).build());
 * list.add(User.builder().name("Anna").password("abc123").mail("anna@mail.com").age(25).build());
 * list.add(User.builder().name("Clara").password("passClara").mail("clara@net.ru").age(8).build());
 * list.add(User.builder().name("Boris").password("123456").mail("boris@yandex.ru").age(31).build());
 * list.add(User.builder().name("Alex").password("adminPass").mail("alex@google.com").age(2).build());
 * <p>
 * return list; }
 */

// Утилитный класс
public final class QuickSort {

    // запрещаем создание экземпляров
    private QuickSort() {
    }

    /**
     * Основной метод для сортировки кастомного SimpleArrayList.
     */

    public static <T> void sort(SimpleArrayList<T> list, Comparator<? super T> comparator) {
        // если список null, пустой, состоит из 1 элемента или нет компаратора — выходим
        if (list == null || list.size() < 2 || comparator == null) {
            return;
        }
        // Запускаем рекурсию: передаем список, индекс первого элемента (0) и индекс последнего (size - 1)
        quickSort(list, 0, list.size() - 1, comparator);
    }

    /**
     * Рекурсивный метод быстрой сортировки.
     */

    // принимает текущие границы сортировки (low и high)
    private static <T> void quickSort(SimpleArrayList<T> list, int low, int high, Comparator<? super T> cmp) {
        // Цикл выполняется, пока левая граница диапазона строго меньше правой границы
        while (low < high) {
            // Выполняем разделение Хоара и получаем индекс границы раздела
            // Перераспределяем элементы вокруг опорного и получаем индекс разделения (границы) половин
            int pivotIndex = partition(list, low, high, cmp);

            // Сначала рекурсивно обрабатываем меньшую подчасть,
            // а большую подчасть обрабатываем итеративно в цикле while.
            // Это гарантирует, что глубина стека вызовов не превысит O(log n).
            // Проверяем, какая из получившихся подчастей по размеру меньше
            if (pivotIndex - low < high - pivotIndex) {
                // Если левая часть меньше, рекурсивно запускаем сортировку для неё (от low до pivotIndex)
                quickSort(list, low, pivotIndex, cmp);
                // Сдвигаем левую границу диапазона вправо
                low = pivotIndex + 1;
            } else { // Если правая часть меньше или равна левой
                // Рекурсивно запускаем сортировку для правой части (от pivotIndex + 1 до high)
                quickSort(list, pivotIndex + 1, high, cmp);
                // Сдвигаем правую границу диапазона влево
                high = pivotIndex;
            }
        }
    }

    /**
     * Схема разбиения Хоара.
     * Двигаем два указателя навстречу друг другу, находя элементы, стоящие не на своих местах
     * относительно опорного элемента (pivot), и меняем их местами.
     */

    private static <T> int partition(SimpleArrayList<T> list, int low, int high, Comparator<? super T> cmp) {
        // За опорный элемент (pivot) берем значение, находящееся ровно посередине текущего отрезка
        T pivot = list.get(low + (high - low) / 2);

        // Инициализируем левый указатель со смещением влево (подготовка к первому шагу цикла do-while)
        int i = low - 1;
        // Инициализируем правый указатель со смещением вправо (подготовка к первому шагу цикла do-while)
        int j = high + 1;

        // Бесконечный цикл, выход из которого произойдет внутри при встрече указателей
        while (true) {
            // Сдвигаем левый указатель вправо, пока элементы меньше опорного
            // Сначала сдвигаем левый указатель вправо на 1 шаг
            do {
                i++;
            } while (cmp.compare(list.get(i), pivot) < 0); // Повторяем, пока текущий элемент меньше опорного

            // Сдвигаем правый указатель влево, пока элементы больше опорного
            // Сначала сдвигаем правый указатель влево на 1 шаг
            do {
                j--;
            } while (cmp.compare(list.get(j), pivot) > 0); // Повторяем, пока текущий элемент больше опорного

            // Если указатели встретились (i == j) или перекрестились (i > j), процесс разбиения завершен
            if (i >= j) {
                return j; // Возвращаем индекс j в качестве финальной точки разделения подмассивов
            }

            // Если указатели остановились и не пересеклись, значит нашли два элемента не на своих местах — меняем их
            swap(list, i, j);
        }
    }

    /**
     * Вспомогательный метод для обмена элементов в SimpleArrayList по их индексам.
     */

    private static <T> void swap(SimpleArrayList<T> list, int i, int j) {
        // Временно сохраняем в переменную temp значение, лежащее по индексу i
        T temp = list.get(i);
        // Записываем на место индекса i значение, взятое из индекса j
        list.set(i, list.get(j));
        // Записываем сохраненное в temp значение на освободившееся место индекса j
        list.set(j, temp);
    }

    // -----------------------------------------------------------------
    // Дополнительное задание: Сортировка четных age
    // -----------------------------------------------------------------

    /**
     * Сортировка объектов в коллекции по числовому полю age
     */

    // Метод работает строго со списком объектов User
    public static void sortEvenAges(SimpleArrayList<User> list) {
        // Если исходный список null или содержит меньше 2 элементов — выходим
        if (list == null || list.size() < 2) {
            return;
        }

        // Объявляем счетчик для определения точного количества пользователей с четным возрастом
        int evenCount = 0;
        // Запускаем цикл по всей исходной коллекции для подсчета четных элементов
        for (int i = 0; i < list.size(); i++) {
            // Побитовое И (& 1) проверяет младший бит числа: если результат 0, то возраст четный
            if ((list.get(i).getAge() & 1) == 0) {
                evenCount++;
            }
        }

        // Если четных элементов нет или он всего один, то сортировать нечего — выходим
        if (evenCount < 2) {
            return;
        }

        // Создаем временный список под четные элементы, выделяя память ровно под подсчитанное количество
        SimpleArrayList<User> evens = new SimpleArrayList<>(evenCount);
        // Запускаем цикл для копирования подходящих пользователей во временный список
        for (int i = 0; i < list.size(); i++) {
            // Получаем объект пользователя по текущему индексу i
            User user = list.get(i);
            // Проверяем возраст пользователя на четность с помощью побитовой операции
            if ((user.getAge() & 1) == 0) {
                evens.add(user); // Если четный — добавляем объект пользователя во временную коллекцию evens
            }
        }

        // Вызываем базовый метод sort нашего класса для сортировки только собранных четных элементов
        // Вторым аргументом передаем лямбда-выражение, которое сравнивает примитивные возраста (int)
        sort(evens, (u1, u2) -> Integer.compare(u1.getAge(), u2.getAge()));

        // Объявляем указатель-индекс для последовательного извлечения элементов из отсортированного списка evens
        int evenIdx = 0;
        // Запускаем финальный цикл по исходной коллекции для вставки отсортированных значений назад
        for (int i = 0; i < list.size(); i++) {
            // Снова находим позиции, где изначально стояли элементы с четным возрастом
            if ((list.get(i).getAge() & 1) == 0) {
                // Перезаписываем элемент в исходном списке отсортированным значением и инкрементируем evenIdx
                list.set(i, evens.get(evenIdx++));
            }
        }
    }
}