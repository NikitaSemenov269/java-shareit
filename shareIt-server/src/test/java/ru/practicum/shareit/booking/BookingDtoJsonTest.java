package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.enums.BookingStatus.WAITING;

class BookingDtoJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void bookingRequestDtoValidation_ShouldFail_WhenNullFields() {
        // Arrange
        BookingRequestDto invalidDto = new BookingRequestDto(
                null, // @NotNull violation
                null, // @NotNull + @FutureOrPresent violation
                null, // @NotNull + @Future violation
                null  // status has default value, so no violation
        );

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(invalidDto);

        // Assert - статус не обязателен, так как есть значение по умолчанию WAITING
        assertThat(violations).hasSize(3);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("itemId"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("start"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void bookingRequestDtoValidation_ShouldFail_WhenInvalidDates() {
        // Arrange
        BookingRequestDto invalidDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().minusDays(1), // @FutureOrPresent violation
                LocalDateTime.now().minusDays(2), // @Future violation
                WAITING
        );

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(invalidDto);

        // Assert
        assertThat(violations).hasSize(2);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("start"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void bookingRequestDtoValidation_ShouldPass_WhenValid() {
        // Arrange
        BookingRequestDto validDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        // Act
        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(validDto);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void bookingDtoDeserializationTest() throws Exception {
        // Arrange
        String jsonContent = """
            {
                "id": 1,
                "start": "2023-12-01T10:00:00",
                "end": "2023-12-02T10:00:00",
                "status": "WAITING",
                "item": {
                    "id": 1,
                    "name": "Test Item"
                },
                "booker": {
                    "id": 1
                }
            }
            """;

        // Act
        BookingDto result = objectMapper.readValue(jsonContent, BookingDto.class);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 2, 10, 0));
        assertThat(result.getStatus()).isEqualTo(WAITING);
        assertThat(result.getItem().getId()).isEqualTo(1L);
        assertThat(result.getItem().getName()).isEqualTo("Test Item");
        assertThat(result.getBooker().getId()).isEqualTo(1L);
    }

    @Test
    void bookingRequestDtoDeserializationTest() throws Exception {
        // Arrange
        String jsonContent = """
            {
                "itemId": 1,
                "start": "2023-12-01T10:00:00",
                "end": "2023-12-02T10:00:00",
                "status": "WAITING"
            }
            """;

        // Act
        BookingRequestDto result = objectMapper.readValue(jsonContent, BookingRequestDto.class);

        // Assert
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 2, 10, 0));
        assertThat(result.getStatus()).isEqualTo(WAITING);
    }
}