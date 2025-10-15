/*
package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.shareit.user.interfaces.UserMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toUserDto_WithValidUser_ShouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        UserDto dto = userMapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void toUserDto_WithNullUser_ShouldReturnNull() {
        UserDto dto = userMapper.toUserDto(null);

        assertNull(dto);
    }

    @Test
    void toUser_WithValidUserRequestDto_ShouldMapCorrectly() {
        UserRequestDto requestDto = new UserRequestDto("Test User", "test@example.com");

        User user = userMapper.toUser(requestDto);

        assertNotNull(user);
        assertNull(user.getId()); // ID should not be set from DTO
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void toUser_WithNullUserRequestDto_ShouldReturnNull() {
        User user = userMapper.toUser(null);

        assertNull(user);
    }

    @Test
    void toUser_WithPartialData_ShouldMapCorrectly() {
        UserRequestDto requestDto = new UserRequestDto(null, "test@example.com");

        User user = userMapper.toUser(requestDto);

        assertNotNull(user);
        assertNull(user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void toUser_WithEmptyStrings_ShouldMapAsIs() {
        UserRequestDto requestDto = new UserRequestDto("", "");

        User user = userMapper.toUser(requestDto);

        assertNotNull(user);
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
    }
}*/
