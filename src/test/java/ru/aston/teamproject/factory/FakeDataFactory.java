package ru.aston.teamproject.factory;

import ru.aston.teamproject.collection.SimpleList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//Фейковые фабрики для тестирования
public class FakeDataFactory {
    private FakeDataFactory(){}

    //Всегда возвращает одно и то же значение
    public static <T> DataFactory<T> fixed(T value){
        return new DataFactory<>() {
            @Override public List<String> fieldNames() {
                return List.of();
            }

            @Override
            public T fromFields(Map<String, String> fields) {
                return value;
            }

            @Override public T random() {
                return value;
            }
        };
    }

    //Ловушка - падает, если её random() вообще вызвали.
    public static <T> DataFactory<T> exploding() {
        return new DataFactory<>() {
            @Override public List<String> fieldNames() {
                return List.of();
            }
            @Override public T fromFields(Map<String, String> f) {
                throw new AssertionError("fromFields не должен вызываться");
            }
            @Override public T random() {
                throw new AssertionError("random() не должен вызываться");
            }
        };
    }

    //Возвращает значения по очереди: "a", "b", "c", потом снова "a"...
    public static DataFactory<String> cycling(String... values) {
        AtomicInteger i = new AtomicInteger();
        return new DataFactory<>() {
            @Override
            public List<String> fieldNames() {
                return List.of();
            }

            @Override
            public String fromFields(Map<String, String> f) {
                return "";
            }

            @Override
            public String random() {
                return values[i.getAndIncrement() % values.length];
            }
        };
    }

    //Считает сколько раз вызвали Random
    public static final class Counting<T> implements DataFactory<T> {
        private final T value;
        private final AtomicInteger calls = new AtomicInteger();

        public Counting(T value) {
            this.value = value;
        }

        @Override
        public List<String> fieldNames() {
            return List.of();
        }

        @Override public T fromFields(Map<String, String> f) {
            return value;
        }
        @Override public T random() {
            calls.incrementAndGet();
            return value;
        }

        public int callCount() {
            return calls.get();
        }
    }
}
