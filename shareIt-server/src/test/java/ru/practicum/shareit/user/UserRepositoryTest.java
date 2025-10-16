package ru.practicum.shareit.user;

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
        // Given
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        var foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserNotExists() {
        // When
        var foundUser = userRepository.findById(999L);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnTrue_WhenEmailExistsForOtherUser() {
        // Given
        User user1 = createUser("User 1", "test@example.com");
        User savedUser1 = entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "other@example.com");
        User savedUser2 = entityManager.persistAndFlush(user2);

        // When
        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", savedUser2.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Given
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmailAndIdNot("nonexistent@example.com", savedUser.getId());

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmailAndIdNot_ShouldReturnFalse_WhenEmailBelongsToSameUser() {
        // Given
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", savedUser.getId());

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    void save_ShouldPersistUserWithGeneratedId() {
        // Given
        User user = createUser("Test User", "test@example.com");

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");

        // Verify in database
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("Test User");
    }

    @Test
    @Transactional
    void save_ShouldThrowException_WhenEmailIsNull() {
        // Given
        User user = new User();
        user.setName("Test User");
        user.setEmail(null);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    @Transactional
    void save_ShouldThrowException_WhenNameIsNull() {
        // Given
        User user = new User();
        user.setName(null);
        user.setEmail("test@example.com");

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    @Transactional
    void save_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        User user1 = createUser("User 1", "duplicate@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "duplicate@example.com");

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @Transactional
    void save_ShouldHandleCaseSensitiveEmail_InPostgreSQL() {
        // Given - PostgreSQL case-sensitive по умолчанию
        User user1 = createUser("User 1", "test@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "TEST@example.com");

        // When
        User savedUser2 = userRepository.save(user2);
        entityManager.flush();

        // Then - в PostgreSQL это РАЗНЫЕ email
        assertThat(savedUser2).isNotNull();
        assertThat(savedUser2.getEmail()).isEqualTo("TEST@example.com");

        // Оба пользователя должны существовать
        List<User> allUsers = userRepository.findAll();
        assertThat(allUsers).hasSize(2);
        assertThat(allUsers)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("test@example.com", "TEST@example.com");
    }

    @Test
    @Transactional
    void deleteById_ShouldRemoveUser() {
        // Given
        User user = createUser("Test User", "test@example.com");
        User savedUser = entityManager.persistAndFlush(user);

        // When
        userRepository.deleteById(savedUser.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        User deletedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(deletedUser).isNull();
    }

    @Test
    @Transactional
    void findAll_ShouldReturnAllUsers() {
        // Given
        User user1 = createUser("User 1", "user1@example.com");
        entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "user2@example.com");
        entityManager.persistAndFlush(user2);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("User 1", "User 2");
    }

    @Test
    @Transactional
    void save_ShouldHandleMaximumLengthName() {
        // Given
        String maxLengthName = "A".repeat(30); // Согласно @Length(max = 30)
        User user = createUser(maxLengthName, "test@example.com");

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser.getName()).hasSize(30);
        assertThat(savedUser.getName()).isEqualTo(maxLengthName);
    }

    @Test
    @Transactional
    void save_ShouldHandleMaximumLengthEmail() {
        // Given
        String localPart = "a".repeat(33); // 33 + "@example.com" (12) = 45
        String maxLengthEmail = localPart + "@example.com";
        User user = createUser("Test User", maxLengthEmail);

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser.getEmail()).isEqualTo(maxLengthEmail);
        assertThat(savedUser.getEmail()).hasSize(45);
    }

    @Test
    @Transactional
    void existsByEmailAndIdNot_ShouldBeCaseSensitive_InPostgreSQL() {
        // Given
        User user1 = createUser("User 1", "test@example.com");
        User savedUser1 = entityManager.persistAndFlush(user1);

        User user2 = createUser("User 2", "other@example.com");
        User savedUser2 = entityManager.persistAndFlush(user2);

        // When - PostgreSQL case-sensitive
        boolean existsLowercase = userRepository.existsByEmailAndIdNot("test@example.com", savedUser2.getId());
        boolean existsUppercase = userRepository.existsByEmailAndIdNot("TEST@example.com", savedUser2.getId());

        // Then
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