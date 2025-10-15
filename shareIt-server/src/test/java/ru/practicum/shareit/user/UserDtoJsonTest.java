package ru.practicum.DTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> userDtoTester;

    @Autowired
    private JacksonTester<UserRequestDto> userRequestDtoTester;

    @Autowired
    private JacksonTester<SimpleUserDto> simpleUserDtoTester;

    private final Validator validator;

    public UserDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // UserDto Tests
    @Test
    void userDto_Serialization_ShouldIncludeAllFields() throws IOException {
        UserDto userDto = new UserDto(1L, "Test User", "test@example.com");

        JsonContent<UserDto> json = userDtoTester.write(userDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void userDto_Deserialization_ShouldCreateObject() throws IOException {
        String json = "{\"id\": 1, \"name\": \"Test User\", \"email\": \"test@example.com\"}";

        UserDto userDto = userDtoTester.parseObject(json);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userDto_Validation_ValidData_ShouldPass() {
        UserDto userDto = new UserDto(1L, "Valid Name", "valid@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void userDto_Validation_BlankName_ShouldFail() {
        UserDto userDto = new UserDto(1L, "   ", "valid@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Имя не может быть пустым полем.");
    }

    @Test
    void userDto_Validation_NullName_ShouldFail() {
        UserDto userDto = new UserDto(1L, null, "valid@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Имя не может быть пустым полем.");
    }

    @Test
    void userDto_Validation_InvalidEmail_ShouldFail() {
        UserDto userDto = new UserDto(1L, "Valid Name", "invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Некорректный формат email.");
    }

    @Test
    void userDto_Validation_BlankEmail_ShouldFail() {
        UserDto userDto = new UserDto(1L, "Valid Name", "   ");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(2); // @NotBlank and @Email violations
    }

    // UserRequestDto Tests
    @Test
    void userRequestDto_Serialization_ShouldIncludeAllFields() throws IOException {
        UserRequestDto requestDto = new UserRequestDto("Test User", "test@example.com");

        JsonContent<UserRequestDto> json = userRequestDtoTester.write(requestDto);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void userRequestDto_Deserialization_ShouldCreateObject() throws IOException {
        String json = "{\"name\": \"Test User\", \"email\": \"test@example.com\"}";

        UserRequestDto requestDto = userRequestDtoTester.parseObject(json);

        assertThat(requestDto.getName()).isEqualTo("Test User");
        assertThat(requestDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userRequestDto_Deserialization_WithNullFields_ShouldCreateObject() throws IOException {
        String json = "{\"name\": null, \"email\": null}";

        UserRequestDto requestDto = userRequestDtoTester.parseObject(json);

        assertThat(requestDto.getName()).isNull();
        assertThat(requestDto.getEmail()).isNull();
    }

    @Test
    void userRequestDto_Validation_ValidData_ShouldPass() {
        UserRequestDto requestDto = new UserRequestDto("Valid Name", "valid@example.com");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void userRequestDto_Validation_InvalidEmail_ShouldFail() {
        UserRequestDto requestDto = new UserRequestDto("Valid Name", "invalid-email");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Некорректный формат email.");
    }

    @Test
    void userRequestDto_Validation_NullFields_ShouldPass() {
        UserRequestDto requestDto = new UserRequestDto(null, null);

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void userRequestDto_Validation_EmptyStringEmail_ShouldFail() {
        UserRequestDto requestDto = new UserRequestDto("Name", "");
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Некорректный формат email.");
    }

    // SimpleUserDto Tests
    @Test
    void simpleUserDto_Serialization_ShouldIncludeOnlyId() throws IOException {
        SimpleUserDto simpleUserDto = new SimpleUserDto(1L);

        JsonContent<SimpleUserDto> json = simpleUserDtoTester.write(simpleUserDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).hasEmptyJsonPathValue("$.name");
        assertThat(json).hasEmptyJsonPathValue("$.email");
    }

    @Test
    void simpleUserDto_Deserialization_ShouldCreateObject() throws IOException {
        String json = "{\"id\": 1}";

        SimpleUserDto simpleUserDto = simpleUserDtoTester.parseObject(json);

        assertThat(simpleUserDto.getId()).isEqualTo(1L);
    }

    @Test
    void simpleUserDto_Deserialization_WithExtraFields_ShouldIgnoreThem() throws IOException {
        String json = "{\"id\": 1, \"name\": \"Test\", \"email\": \"test@example.com\"}";

        SimpleUserDto simpleUserDto = simpleUserDtoTester.parseObject(json);

        assertThat(simpleUserDto.getId()).isEqualTo(1L);
    }

    @Test
    void simpleUserDto_Validation_ValidData_ShouldPass() {
        SimpleUserDto simpleUserDto = new SimpleUserDto(1L);

        Set<ConstraintViolation<SimpleUserDto>> violations = validator.validate(simpleUserDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void simpleUserDto_Validation_NullId_ShouldPass() {
        SimpleUserDto simpleUserDto = new SimpleUserDto(null);

        Set<ConstraintViolation<SimpleUserDto>> violations = validator.validate(simpleUserDto);

        assertThat(violations).isEmpty();
    }

    @Test
    void allDtos_WithExtremeValues_ShouldHandleGracefully() throws IOException {
        // UserDto with max values
        UserDto userDto = new UserDto(Long.MAX_VALUE, "A".repeat(100), "test@example.com");
        assertThat(userDtoTester.write(userDto)).isNotNull();

        // UserRequestDto with max values
        UserRequestDto requestDto = new UserRequestDto("A".repeat(100), "test@example.com");
        assertThat(userRequestDtoTester.write(requestDto)).isNotNull();

        // SimpleUserDto with max value
        SimpleUserDto simpleDto = new SimpleUserDto(Long.MAX_VALUE);
        assertThat(simpleUserDtoTester.write(simpleDto)).isNotNull();
    }

    @Test
    void allDtos_WithSpecialCharacters_ShouldSerializeCorrectly() throws IOException {
        UserDto userDto = new UserDto(1L, "User with spéciäl chàrs", "test+filter@example.com");

        JsonContent<UserDto> json = userDtoTester.write(userDto);

        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo("User with spéciäl chàrs");
        assertThat(json).extractingJsonPathStringValue("$.email")
                .isEqualTo("test+filter@example.com");
    }
}