package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.shareit.user.interfaces.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserDto_ShouldMapAllFields() {
        User user = new User(1L, "Test User", "test@example.com");

        UserDto userDto = userMapper.toUserDto(user);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUser_ShouldMapAllFields() {
        UserRequestDto requestDto = new UserRequestDto("Test User", "test@example.com");

        User user = userMapper.toUser(requestDto);

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUser_ShouldHandleNullRequestDto() {
        User user = userMapper.toUser(null);

        assertThat(user).isNull();
    }

    @Test
    void toUserDto_ShouldHandleNullUser() {
        UserDto userDto = userMapper.toUserDto(null);

        assertThat(userDto).isNull();
    }

    @Test
    void toUser_ShouldHandleEmptyName() {
        UserRequestDto requestDto = new UserRequestDto("", "test@example.com");

        User user = userMapper.toUser(requestDto);

        assertThat(user.getName()).isEmpty();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUser_ShouldHandleNullName() {UserRequestDto requestDto = new UserRequestDto(null, "test@example.com");

        User user = userMapper.toUser(requestDto);

        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUser_ShouldHandleNullEmail() {
        UserRequestDto requestDto = new UserRequestDto("Test User", null);

        User user = userMapper.toUser(requestDto);

        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void toUserDto_ShouldHandleEmptyUser() {
        User user = new User();

        UserDto userDto = userMapper.toUserDto(user);

        assertThat(userDto.getId()).isNull();
        assertThat(userDto.getName()).isNull();
        assertThat(userDto.getEmail()).isNull();
    }

    @Test
    void toUser_ShouldHandleEmptyRequestDto() {
        UserRequestDto requestDto = new UserRequestDto();

        User user = userMapper.toUser(requestDto);

        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    void toUserDto_ShouldHandleUserWithEmptyFields() {
        User user = new User(1L, "", "");

        UserDto userDto = userMapper.toUserDto(user);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEmpty();
        assertThat(userDto.getEmail()).isEmpty();
    }
}