/*
package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.practicum.DTO.CommentDto;
import ru.practicum.DTO.CommentRequestDto;
import ru.practicum.DTO.CommentTextDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> commentDtoTester;

    @Autowired
    private JacksonTester<CommentRequestDto> commentRequestDtoTester;

    private final Validator validator;

    public CommentDtoJsonTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void commentRequestDto_WithValidText_ShouldPassValidation() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Valid comment text");
        dto.setItemId(1L);
        dto.setUserId(2L);

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void commentRequestDto_WithLongText_ShouldFailValidation() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("A".repeat(201));
        dto.setItemId(1L);
        dto.setUserId(2L);

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void commentDto_Serialization_ShouldIncludeAllFields() throws Exception {
        CommentDto dto = new CommentDto(1L, "Great item!", "John Doe",
                LocalDateTime.of(2024, 1, 1, 10, 0));

        var json = commentDtoTester.write(dto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(json).extractingJsonPathStringValue("$.authorName").isEqualTo("John Doe");
    }

    @Test
    void commentTextDto_WithEmptyText_ShouldFailValidation() {
        CommentTextDto dto = new CommentTextDto();
        dto.setText("");

        Set<ConstraintViolation<CommentTextDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}*/
