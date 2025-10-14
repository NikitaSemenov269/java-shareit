package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private UserRequestDto validUserRequest;

    @BeforeEach
    void setUp() {
        validUserRequest = new UserRequestDto("Test User", "test@example.com");
        userRepository.deleteAll(); // Явная очистка перед каждым тестом
    }

    // Positive test cases
    @Test
    void createUser_WithValidData_ShouldCreateAndReturnUser() {
        UserDto result = userService.createUser(validUserRequest);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertTrue(userRepository.existsById(result.getId()));
    }

    @Test
    void createUser_WithBoundaryNameLength_ShouldSuccess() {
        String boundaryName = "A".repeat(30);
        UserRequestDto request = new UserRequestDto(boundaryName, "boundary@example.com");

        UserDto result = userService.createUser(request);

        assertNotNull(result.getId());
        assertEquals(boundaryName, result.getName());
    }

    @Test
    void createUser_WithMinimalValidData_ShouldSuccess() {
        UserRequestDto minimalRequest = new UserRequestDto("A", "a@b.c");

        UserDto result = userService.createUser(minimalRequest);

        assertNotNull(result.getId());
        assertEquals("A", result.getName());
        assertEquals("a@b.c", result.getEmail());
    }

    @Test
    void updateUser_WithAllFields_ShouldUpdateSuccessfully() {
        UserDto created = userService.createUser(validUserRequest);
        UserRequestDto updateRequest = new UserRequestDto("Updated Name", "updated@example.com");

        UserDto result = userService.updateUser(created.getId(), updateRequest);

        assertEquals(created.getId(), result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void updateUser_WithPartialNameUpdate_ShouldUpdateOnlyName() {
        UserDto created = userService.createUser(validUserRequest);
        UserRequestDto updateRequest = new UserRequestDto("Updated Name", null);

        UserDto result = userService.updateUser(created.getId(), updateRequest);

        assertEquals("Updated Name", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void updateUser_WithPartialEmailUpdate_ShouldUpdateOnlyEmail() {
        UserDto created = userService.createUser(validUserRequest);
        UserRequestDto updateRequest = new UserRequestDto(null, "newemail@example.com");

        UserDto result = userService.updateUser(created.getId(), updateRequest);

        assertEquals("Test User", result.getName());
        assertEquals("newemail@example.com", result.getEmail());
    }

    @Test
    void updateUser_WithSameEmail_ShouldNotThrowException() {
        UserDto created = userService.createUser(validUserRequest);

        assertDoesNotThrow(() -> {
            UserDto result = userService.updateUser(created.getId(),
                    new UserRequestDto("New Name", "test@example.com"));
            assertEquals("New Name", result.getName());
        });
    }

    @Test
    void updateUser_WithEmptyName_ShouldUpdateSuccessfully() {
        UserDto created = userService.createUser(validUserRequest);

        // В UserRequestDto name может быть пустым - это разрешено валидацией
        UserRequestDto update = new UserRequestDto("", "updated@example.com");

        UserDto result = userService.updateUser(created.getId(), update);

        assertEquals("", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void getUserDtoById_WithExistingUser_ShouldReturnUser() {
        UserDto created = userService.createUser(validUserRequest);

        UserDto result = userService.getUserDtoById(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void deleteUser_WithExistingUser_ShouldRemoveUser() {
        UserDto created = userService.createUser(validUserRequest);
        Long userId = created.getId();

        // Проверяем, что пользователь существует до удаления
        assertTrue(userRepository.existsById(userId));

        userService.deleteUser(userId);

        // Проверяем, что пользователь удален из репозитория
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> userService.deleteUser(999L));
    }

    // Negative test cases
    @Test
    void createUser_WithDuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        userService.createUser(validUserRequest);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(validUserRequest);
        });
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowNotFoundException() {
        UserRequestDto updateRequest = new UserRequestDto("Updated", "updated@example.com");

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(999L, updateRequest);
        });
    }

    @Test
    void updateUser_WithDuplicateEmailFromOtherUser_ShouldThrowEmailAlreadyExistsException() {
        UserDto user1 = userService.createUser(validUserRequest);
        UserDto user2 = userService.createUser(new UserRequestDto("User2", "user2@example.com"));

        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(user2.getId(), new UserRequestDto("User2", "test@example.com"));
        });
    }

    @Test
    void getUserDtoById_WithNonExistentId_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserDtoById(999L);
        });
    }

    // Complex scenarios
    @Test
    void createMultipleUsers_ThenUpdateAndDelete_ShouldWorkCorrectly() {
        // Create multiple users
        UserDto user1 = userService.createUser(new UserRequestDto("User1", "user1@example.com"));
        UserDto user2 = userService.createUser(new UserRequestDto("User2", "user2@example.com"));
        UserDto user3 = userService.createUser(new UserRequestDto("User3", "user3@example.com"));

        // Update user2
        UserDto updatedUser2 = userService.updateUser(user2.getId(),
                new UserRequestDto("Updated User2", "updated2@example.com"));

        // Delete user3
        userService.deleteUser(user3.getId());

        // Verify results
        assertEquals("Updated User2", updatedUser2.getName());
        assertEquals("updated2@example.com", updatedUser2.getEmail());

        // user3 должен быть удален
        assertFalse(userRepository.existsById(user3.getId()));

        // user1 и user2 должны остаться
        UserDto retrievedUser1 = userService.getUserDtoById(user1.getId());
        assertEquals("User1", retrievedUser1.getName());

        UserDto retrievedUser2 = userService.getUserDtoById(user2.getId());
        assertEquals("Updated User2", retrievedUser2.getName());
    }

    @Test
    void updateUser_WithOnlyWhitespaceName_ShouldUpdateSuccessfully() {
        UserDto created = userService.createUser(validUserRequest);

        UserRequestDto updateRequest = new UserRequestDto("   ", "updated@example.com");

        UserDto result = userService.updateUser(created.getId(), updateRequest);

        assertEquals("   ", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    // Тест метода isEmailConflict (через reflection для приватного метода)
    @Test
    void isEmailConflict_WithVariousPostgresErrors_ShouldDetectEmailConflicts() throws Exception {
        // Используем reflection для тестирования приватного метода
        var method = UserServiceImpl.class.getDeclaredMethod("isEmailConflict", DataIntegrityViolationException.class);
        method.setAccessible(true);

        UserServiceImpl service = new UserServiceImpl(userRepository, null);

        // Test different PostgreSQL error messages
        assertTrue((Boolean) method.invoke(service,
                new org.springframework.dao.DataIntegrityViolationException(
                        "ERROR: duplicate key value violates unique constraint \"users_email_key\"")));

        assertTrue((Boolean) method.invoke(service,
                new org.springframework.dao.DataIntegrityViolationException(
                        "ERROR: duplicate key value violates unique constraint")));

        assertTrue((Boolean) method.invoke(service,
                new org.springframework.dao.DataIntegrityViolationException(
                        "SQL Error: 23505, SQLState: 23505")));

        assertFalse((Boolean) method.invoke(service,
                new org.springframework.dao.DataIntegrityViolationException(
                        "Some other constraint violation")));
    }

    @Test
    void updateUser_WithNullRequest_ShouldThrowException() {
        UserDto created = userService.createUser(validUserRequest);

        // Null проверка должна быть в сервисе
        assertThrows(NullPointerException.class, () -> {
            userService.updateUser(created.getId(), null);
        });
    }

    @Test
    void createUser_WithNullRequest_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            userService.createUser(null);
        });
    }
}
