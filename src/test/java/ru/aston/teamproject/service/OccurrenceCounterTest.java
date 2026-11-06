package ru.aston.teamproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.teamproject.user.User;
import ru.aston.teamproject.collection.SimpleArrayList;
import ru.aston.teamproject.collection.SimpleList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OccurrenceCounterTest {

    private SimpleList<User> users;
    private User anna;

    @BeforeEach
    void setUp() {
        users = new SimpleArrayList<>(10);
        anna = User.builder().name("Anna").password("p1").mail("a@mail.com").age(20).build();
    }

    @Test
    void whenEmptyCollection_thenZero() throws Exception {
        assertThat(OccurrenceCounter.countByUser(users, anna, 4)).isZero();
    }

    @Test
    void whenTargetAbsent_thenZero() throws Exception {
        users.add(User.builder().name("Boris").password("p2").mail("b@mail.com").age(30).build());
        assertThat(OccurrenceCounter.countByUser(users, anna, 2)).isZero();
    }

    @Test
    void whenTargetPresentMultipleTimes_thenCounted() throws Exception {
        users.add(anna);
        users.add(User.builder().name("Boris").password("p2").mail("b@mail.com").age(30).build());
        users.add(anna);
        users.add(anna);
        assertThat(OccurrenceCounter.countByUser(users, anna, 3)).isEqualTo(3);
    }

    @Test
    void whenThreadsDoNotChangeResult() throws Exception {
        for (int i = 0; i < 100; i++) {
            users.add(anna);
            users.add(User.builder()
                    .name("U" + i).password("p").mail("m" + i + "@x.com").age(30).build());
        }
        int single = OccurrenceCounter.countByUser(users, anna, 1);
        int many = OccurrenceCounter.countByUser(users, anna, 8);
        int more = OccurrenceCounter.countByUser(users, anna, 200); // threads > size
        assertThat(single).isEqualTo(100);
        assertThat(many).isEqualTo(100);
        assertThat(more).isEqualTo(100);
    }

    @Test
    void whenNonPositiveThreads_thenException() {
        assertThatThrownBy(() -> OccurrenceCounter.countByUser(users, anna, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> OccurrenceCounter.countByUser(users, anna, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenNullArgumentsThenException() {
        assertThatThrownBy(() -> OccurrenceCounter.countByUser(null, anna, 2))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> OccurrenceCounter.countByUser(users, null, 2))
                .isInstanceOf(NullPointerException.class);
    }
}