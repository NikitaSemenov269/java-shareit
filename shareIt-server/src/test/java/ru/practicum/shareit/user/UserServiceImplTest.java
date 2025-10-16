package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.exception.DataIntegrityException;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserMapper;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final Long userId = 1L;
    private final String userName = "Test User";
    private final String userEmail = "test@example.com";

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        UserRequestDto requestDto = new UserRequestDto(userName, userEmail);
        User user = new User(null, userName, userEmail);
        User savedUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, userName, userEmail);

        when(mapper.toUser(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(mapper.toUserDto(savedUser)).thenReturn(expectedDto);

        UserDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userName, result.getName());
        assertEquals(userEmail, result.getEmail());
        verify(userRepository).save(user);
        verify(mapper).toUser(requestDto);
        verify(mapper).toUserDto(savedUser);
    }

    @Test
    void createUser_WhenEmailConflict_ShouldThrowEmailAlreadyExistsException() {
        UserRequestDto requestDto = new UserRequestDto(userName, userEmail);
        User user = new User(null, userName, userEmail);
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint \"users_email_key\"");

        when(mapper.toUser(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(exception);

        EmailAlreadyExistsException thrown = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(requestDto));

        assertEquals("Пользователь с таким email уже существует.", thrown.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_WhenOtherDataIntegrityViolation_ShouldThrowDataIntegrityException() {
        UserRequestDto requestDto = new UserRequestDto(userName, userEmail);
        User user = new User(null, userName, userEmail);
        DataIntegrityViolationException exception = new DataIntegrityViolationException("other constraint");

        when(mapper.toUser(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(exception);

        DataIntegrityException thrown = assertThrows(DataIntegrityException.class,
                () -> userService.createUser(requestDto));

        assertEquals("Ошибка сохранения данных", thrown.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldUpdateBothNameAndEmail_WhenBothProvided() {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", "updated@example.com");
        User existingUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, "Updated Name", "updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("updated@example.com", userId)).thenReturn(false);
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository).existsByEmailAndIdNot("updated@example.com", userId);
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowNotFoundException() {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", "updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, requestDto));

        assertTrue(thrown.getMessage().contains("Пользователь с " + userId + " не существует"));
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void updateUser_WhenEmailAlreadyTaken_ShouldThrowEmailAlreadyExistsException() {
        UserRequestDto requestDto = new UserRequestDto(userName, "taken@example.com");
        User existingUser = new User(userId, userName, userEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("taken@example.com", userId)).thenReturn(true);

        EmailAlreadyExistsException thrown = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.updateUser(userId, requestDto));

        assertEquals("Email уже занят другим пользователем.", thrown.getMessage());
        assertEquals(userEmail, existingUser.getEmail());
    }

    @Test
    void updateUser_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", null);
        User existingUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, "Updated Name", userEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals("Updated Name", result.getName());
        assertEquals(userEmail, result.getEmail());
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail_WhenOnlyEmailProvided() {
        UserRequestDto requestDto = new UserRequestDto(null, "updated@example.com");
        User existingUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, userName, "updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("updated@example.com", userId)).thenReturn(false);
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals(userName, result.getName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository).existsByEmailAndIdNot("updated@example.com", userId);
    }

    @Test
    void updateUser_ShouldNotCheckEmail_WhenEmailNotChanged() {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", userEmail);
        User existingUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, "Updated Name", userEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals("Updated Name", result.getName());
        assertEquals(userEmail, result.getEmail());
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void updateUser_ShouldCheckEmailUniqueness_WhenEmailCaseChanged() {
        UserRequestDto requestDto = new UserRequestDto(userName, "NEW@example.com");
        User existingUser = new User(userId, userName, "old@example.com");
        UserDto expectedDto = new UserDto(userId, userName, "NEW@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("NEW@example.com", userId)).thenReturn(false);
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals("NEW@example.com", result.getEmail());
        verify(userRepository).existsByEmailAndIdNot("NEW@example.com", userId);
    }

    @Test
    void updateUser_WhenBothFieldsNull_ShouldReturnOriginalUser() {
        UserRequestDto requestDto = new UserRequestDto(null, null);
        User existingUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, userName, userEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals(userName, result.getName());
        assertEquals(userEmail, result.getEmail());
        verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    @Test
    void getUserDtoById_ShouldReturnUserDto() {
        User user = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, userName, userEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserDtoById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userName, result.getName());
        assertEquals(userEmail, result.getEmail());
        verify(userRepository).findById(userId);
        verify(mapper).toUserDto(user);
    }

    @Test
    void getUserDtoById_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userService.getUserDtoById(userId));

        assertEquals("Пользователь с ID: " + userId + " не найден", thrown.getMessage());
        verify(userRepository).findById(userId);
        verify(mapper, never()).toUserDto(any());
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldNotThrow() {
        Long nonExistentId = 999L;
        doNothing().when(userRepository).deleteById(nonExistentId);

        assertDoesNotThrow(() -> userService.deleteUser(nonExistentId));

        verify(userRepository).deleteById(nonExistentId);
    }

    @Test
    void updateUser_ShouldHandleEmptyName() {
        UserRequestDto requestDto = new UserRequestDto("", "updated@example.com");
        User existingUser = new User(userId, userName, userEmail);
        UserDto expectedDto = new UserDto(userId, "", "updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("updated@example.com", userId)).thenReturn(false);
        when(mapper.toUserDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(userId, requestDto);

        assertEquals("", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }
}