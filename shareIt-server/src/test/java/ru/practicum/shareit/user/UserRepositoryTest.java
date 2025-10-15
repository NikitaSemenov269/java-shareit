package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findById_WithExistingUser_ShouldReturnUser() {
        User user = createTestUser("Test User", "test@example.com");
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Test User", found.get().getName());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void existsByEmailAndIdNot_WithDifferentUserSameEmail_ShouldReturnTrue() {
        User user1 = createTestUser("User1", "same@example.com");
        User saved1 = userRepository.save(user1);

        User user2 = createTestUser("User2", "different@example.com");
        User saved2 = userRepository.save(user2);
        boolean exists = userRepository.existsByEmailAndIdNot("same@example.com", saved2.getId());

        assertTrue(exists);
    }

    @Test
    void existsByEmailAndIdNot_WithSameUser_ShouldReturnFalse() {
        User user = createTestUser("Test User", "test@example.com");
        User saved = userRepository.save(user);

        boolean exists = userRepository.existsByEmailAndIdNot("test@example.com", saved.getId());

        assertFalse(exists);
    }

    @Test
    void existsByEmailAndIdNot_WithNonExistingEmail_ShouldReturnFalse() {
        User user = createTestUser("Test User", "test@example.com");
        User saved = userRepository.save(user);

        boolean exists = userRepository.existsByEmailAndIdNot("nonexistent@example.com", saved.getId());

        assertFalse(exists);
    }

    @Test
    void existsByEmailAndIdNot_WithNullEmail_ShouldReturnFalse() {
        User user = createTestUser("Test User", "test@example.com");
        User saved = userRepository.save(user);

        boolean exists = userRepository.existsByEmailAndIdNot(null, saved.getId());

        assertFalse(exists);
    }

    @Test
    void save_WithDuplicateEmail_ShouldThrowException() {
        User user1 = createTestUser("User1", "duplicate@example.com");
        userRepository.save(user1);

        User user2 = createTestUser("User2", "duplicate@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2); // Используем saveAndFlush для немедленного исключения
        });
    }

    @Test
    void save_WithNullEmail_ShouldThrowException() {
        User user = new User();
        user.setName("Test User");
        user.setEmail(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    void save_WithNullName_ShouldThrowException() {
        User user = new User();
        user.setName(null);
        user.setEmail("test@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    void save_WithEmptyName_ShouldSuccess() {
        User user = new User();
        user.setName("");
        user.setEmail("test@example.com");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("", saved.getName());
        assertEquals("test@example.com", saved.getEmail());
    }

    @Test
    void save_WithEmptyEmail_ShouldThrowException() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user);
        });
    }

    @Test
    void findAll_WithMultipleUsers_ShouldReturnAll() {
        User user1 = createTestUser("User1", "user1@example.com");
        User user2 = createTestUser("User2", "user2@example.com");
        User user3 = createTestUser("User3", "user3@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> allUsers = userRepository.findAll();

        assertEquals(3, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("User1")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("User2")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("User3")));
    }

    @Test
    void deleteById_WithExistingUser_ShouldRemoveUser() {
        User user = createTestUser("Test User", "test@example.com");
        User saved = userRepository.save(user);

        assertTrue(userRepository.existsById(saved.getId()));

        userRepository.deleteById(saved.getId());

        assertFalse(userRepository.existsById(saved.getId()));
    }

    @Test
    void deleteById_WithNonExistingId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> userRepository.deleteById(999L));
    }

    @Test
    void count_WithUsers_ShouldReturnCorrectCount() {
        long initialCount = userRepository.count();

        User user1 = createTestUser("User1", "user1@example.com");
        User user2 = createTestUser("User2", "user2@example.com");

        userRepository.save(user1);
        userRepository.save(user2);

        assertEquals(initialCount + 2, userRepository.count());
    }

    @Test
    void save_WithMaxLengthFields_ShouldSuccess() {
        String maxName = "A".repeat(30);
        String maxEmail = "A".repeat(45);

        User user = new User();
        user.setName(maxName);
        user.setEmail(maxEmail);

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals(maxName, saved.getName());
        assertEquals(maxEmail, saved.getEmail());
    }

    private User createTestUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}