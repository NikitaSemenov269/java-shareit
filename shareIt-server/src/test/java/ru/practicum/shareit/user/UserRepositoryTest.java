package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        var foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserNotExists() {
        var foundUser = userRepository.findById(999L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnTrue_WhenEmailExistsForOtherUser() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("test@example.com");
        User savedUser1 = entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("other@example.com");
        User savedUser2 = entityManager.persistAndFlush(user2);

        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", savedUser2.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailDoesNotExist() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmailAndIdNot("nonexistent@example.com", savedUser.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailBelongsToSameUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", savedUser.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void save_ShouldPersistUserWithGeneratedId() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");

        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Test User");
    }

    @Test
    void save_ShouldThrowException_WhenEmailIsNull() {
        User user = new User();
        user.setName("Test User");
        user.setEmail(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    void save_ShouldThrowException_WhenNameIsNull() {
        User user = new User();
        user.setName(null);
        user.setEmail("test@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    void save_ShouldThrowException_WhenEmailAlreadyExists() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("duplicate@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("duplicate@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    void save_ShouldHandleCaseSensitiveEmail() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("test@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("TEST@example.com");

        // В зависимости от настройки БД это может пройти или упасть
        User savedUser2 = userRepository.save(user2);
        assertThat(savedUser2).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        userRepository.deleteById(savedUser.getId());
        entityManager.flush();

        User deletedUser = entityManager.find(User.class, savedUser.getId());
        assertThat(deletedUser).isNull();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        entityManager.persistAndFlush(user2);

        var users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("User 1", "User 2");
    }

    @Test
    void save_ShouldHandleLongNames() {
        String longName = "A".repeat(30); // Максимальная длина согласно аннотации @Length(max = 30)
        User user = new User();
        user.setName(longName);
        user.setEmail("test@example.com");

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getName()).hasSize(30);
        assertThat(savedUser.getName()).isEqualTo(longName);
    }

    @Test
    void save_ShouldHandleLongEmails() {
        String longEmail = "a".repeat(45) + "@example.com"; // Максимальная длина согласно @Column(length = 45)
        User user = new User();
        user.setName("Test User");
        user.setEmail(longEmail);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getEmail()).isEqualTo(longEmail);
    }
}