package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.DTO.SimpleItemDto;
import ru.practicum.DTO.SimpleUserDto;
import ru.practicum.enums.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Регистрируем модуль для работы с Java Time
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void bookingRequestDto_Serialization_ShouldIncludeAllFields() throws JsonProcessingException {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        dto.setStart(start);
        dto.setEnd(end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\"");
        assertThat(json).contains("\"end\"");
    }

    @Test
    void bookingRequestDto_Deserialization_ShouldCreateValidObject() throws JsonProcessingException {
        String json = "{\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}";

        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 2, 10, 0));
    }

    @Test
    void bookingDto_Serialization_ShouldIncludeAllFields() throws JsonProcessingException {
        SimpleItemDto item = new SimpleItemDto(1L, "Test Item");
        SimpleUserDto booker = new SimpleUserDto(2L);

        // Предполагаем, что BookingDto имеет конструктор или сеттеры
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));
        dto.setStatus(BookingStatus.WAITING);
        dto.setItem(item);
        dto.setBooker(booker);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"item\"");
        assertThat(json).contains("\"booker\"");
    }

    @Test
    void simpleItemDto_Serialization_ShouldIncludeIdAndName() throws JsonProcessingException {
        SimpleItemDto dto = new SimpleItemDto(1L, "Test Item");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
    }

    @Test
    void simpleUserDto_Serialization_ShouldIncludeOnlyId() throws JsonProcessingException {
        SimpleUserDto dto = new SimpleUserDto(1L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"id\":1}");
    }
}