package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userDto_ShouldSerializeCorrectly() throws JsonProcessingException {
        UserDto userDto = new UserDto(1L, "Test User", "test@example.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void userDto_ShouldDeserializeCorrectly() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userRequestDto_ShouldSerializeCorrectly() throws JsonProcessingException {
        UserRequestDto requestDto = new UserRequestDto("Test User", "test@example.com");

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void userRequestDto_ShouldDeserializeCorrectly() throws JsonProcessingException {
        String json = "{\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        UserRequestDto requestDto = objectMapper.readValue(json, UserRequestDto.class);

        assertThat(requestDto.getName()).isEqualTo("Test User");
        assertThat(requestDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userRequestDto_ShouldHandleNullValues() throws JsonProcessingException {
        UserRequestDto requestDto = new UserRequestDto(null, null);

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"name\":null");
        assertThat(json).contains("\"email\":null");
    }

    @Test
    void userDto_ShouldHandleNullValues() throws JsonProcessingException {
        UserDto userDto = new UserDto(null, null, null);

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"id\":null");
        assertThat(json).contains("\"name\":null");
        assertThat(json).contains("\"email\":null");
    }

    @Test
    void userRequestDto_ShouldHandleEmptyStrings() throws JsonProcessingException {
        UserRequestDto requestDto = new UserRequestDto("", "");

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"name\":\"\"");
        assertThat(json).contains("\"email\":\"\"");
    }

    @Test
    void userDto_ShouldHandleEmptyStrings() throws JsonProcessingException {
        UserDto userDto = new UserDto(1L, "", "");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"\"");
        assertThat(json).contains("\"email\":\"\"");
    }

    @Test
    void userDto_ShouldHandleSpecialCharacters() throws JsonProcessingException {
        UserDto userDto = new UserDto(1L, "User Name ÖÄÜ", "test+special@example.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"name\":\"User Name ÖÄÜ\"");
        assertThat(json).contains("\"email\":\"test+special@example.com\"");
    }

    @Test
    void userRequestDto_ShouldHandleLongEmail() throws JsonProcessingException {
        String longEmail = "very.long.email.address.that.is.still.valid@example.com";
        UserRequestDto requestDto = new UserRequestDto("Test User", longEmail);

        String json = objectMapper.writeValueAsString(requestDto);
        UserRequestDto deserialized = objectMapper.readValue(json, UserRequestDto.class);

        assertThat(deserialized.getEmail()).isEqualTo(longEmail);
    }
}