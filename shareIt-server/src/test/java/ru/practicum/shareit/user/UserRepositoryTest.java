package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User user = createUser("Test User", "test@example.com");
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
        User user1 = createUser("User 1", "test@example.com");
        User savedUser1 = entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "other@example.com");
        User savedUser2 = entityManager.persistAndFlush(user2);

        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", savedUser2.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailDoesNotExist() {
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmailAndIdNot("nonexistent@example.com", savedUser.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailBelongsToSameUser() {
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", savedUser.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    void save_ShouldPersistUserWithGeneratedId() {

        User user = createUser("Test User", "test@example.com");

        User savedUser = userRepository.save(user);
        entityManager.flush();

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");

        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Test User");
    }

    @Test
    @Transactional
    void save_ShouldThrowException_WhenEmailIsNull() {
        User user = new User();
        user.setName("Test User");
        user.setEmail(null);

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    @Transactional
    void save_ShouldThrowException_WhenNameIsNull() {
        User user = new User();
        user.setName(null);
        user.setEmail("test@example.com");

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }
    @Test
    @Transactional
    void save_ShouldThrowException_WhenEmailAlreadyExists() {
        User user1 = createUser("User 1", "duplicate@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "duplicate@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @Transactional
    void save_ShouldHandleCaseSensitiveEmail_InPostgreSQL() {
        User user1 = createUser("User 1", "test@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "TEST@example.com");

        User savedUser2 = userRepository.save(user2);
        entityManager.flush();

        assertThat(savedUser2).isNotNull();
        assertThat(savedUser2.getEmail()).isEqualTo("TEST@example.com");

        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("test@example.com", "TEST@example.com");
    }

    @Test
    @Transactional
    void deleteById_ShouldRemoveUser() {
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        userRepository.deleteById(savedUser.getId());
        entityManager.flush();
        entityManager.clear();

        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(deletedUser).isNull();
    }

    @Test
    @Transactional
    void findAll_ShouldReturnAllUsers() {
        User user1 = createUser("User 1", "user1@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "user2@example.com");
        entityManager.persistAndFlush(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("User 1", "User 2");
    }

    @Test
    @Transactional
    void save_ShouldHandleMaximumLengthName() {
        String maxLengthName = "A".repeat(30); // Согласно @Length(max = 30)
        User user = createUser(maxLengthName, "test@example.com");

        User savedUser = userRepository.save(user);
        entityManager.flush();

        assertThat(savedUser.getName()).hasSize(30);
        assertThat(savedUser.getName()).isEqualTo(maxLengthName);
    }

    @Test
    @Transactional
    void save_ShouldHandleMaximumLengthEmail() {
        String localPart = "a".repeat(33); // 33 + "@example.com" (12) = 45
        String maxLengthEmail = localPart + "@example.com";
        User user = createUser("Test User", maxLengthEmail);

        User savedUser = userRepository.save(user);
        entityManager.flush();

        assertThat(savedUser.getEmail()).isEqualTo(maxLengthEmail);
        assertThat(savedUser.getEmail()).hasSize(45);
    }

    @Test
    @Transactional
    void existsByEmailAndIdNot_ShouldBeCaseSensitive_InPostgreSQL() {
        User user1 = createUser("User 1", "test@example.com");
        User savedUser1 = entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "other@example.com");
        User savedUser2 = entityManager.persistAndFlush(user2);

        boolean existsLowercase = userRepository.existsByEmailAndIdNot("test@example.com", savedUser2.getId());
        boolean existsUppercase = userRepository.existsByEmailAndIdNot("TEST@example.com", savedUser2.getId());

        assertThat(existsLowercase).isTrue();  // Находит точное совпадение
        assertThat(existsUppercase).isFalse(); // Не находит из-за разного регистра
    }

    @Test
    @Transactional
    void updateUser_ShouldChangeUserData() {
        User user = createUser("Original Name", "original@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(foundUser.getName()).isEqualTo("Updated Name");
        assertThat(foundUser.getEmail()).isEqualTo("updated@example.com");
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}