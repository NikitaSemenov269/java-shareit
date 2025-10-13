package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.interfaces.UserMapper;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserDtoById_UserNotFound_ThrowsException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserDtoById(userId);
        });

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserDtoById_UserFound_ReturnsMappedDto() {
        Long userId = 1L;
        User user = new User(userId, "John", "john@mail.ru");
        UserDto expectedDto = new UserDto(userId, "John", "john@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserDtoById(userId);

        assertNotNull(result);
        assertEquals("John", result.getName());
        verify(userMapper).toUserDto(user);
    }

    @Test
    void createUser_Success() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("John");
        requestDto.setEmail("john@mail.ru");

        User user = new User();
        user.setName("John");

        UserDto expectedDto = new UserDto();
        expectedDto.setName("John");

        when(userMapper.toUser(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        verify(userMapper).toUser(requestDto);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void deleteUser_Success() {
        Long userId = 1L;
        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
        verifyNoInteractions(userMapper);
    }
}