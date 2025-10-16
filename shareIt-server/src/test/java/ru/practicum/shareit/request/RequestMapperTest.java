package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RequestMapperTest {

    private final RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Test
    void toDto_ShouldMapRequestToResponseRequestDto() {
        User requester = new User();
        requester.setId(1L);

        Request request = new Request();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequester(requester);
        LocalDateTime created = LocalDateTime.now();
        request.setCreated(created);

        ResponseRequestDto dto = requestMapper.toDto(request);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(1L, dto.getRequesterId());
        assertEquals(created, dto.getCreated());
    }

    @Test
    void toDto_WhenRequesterIsNull_ShouldMapWithNullRequesterId() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequester(null);
        request.setCreated(LocalDateTime.now());

        ResponseRequestDto dto = requestMapper.toDto(request);

        assertNotNull(dto);
        assertNull(dto.getRequesterId());
    }

    @Test
    void toDto_WhenRequestIsNull_ShouldReturnNull() {
        ResponseRequestDto dto = requestMapper.toDto(null);
        assertNull(dto);
    }

    // ДОБАВЛЕН тест для проверки маппинга всех полей
    @Test
    void toDto_ShouldMapAllFieldsCorrectly() {
        User requester = new User();
        requester.setId(5L);

        Request request = new Request();
        request.setId(10L);
        request.setDescription("Test description");
        request.setRequester(requester);
        LocalDateTime created = LocalDateTime.of(2023, 1, 1, 12, 0);
        request.setCreated(created);

        ResponseRequestDto dto = requestMapper.toDto(request);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("Test description", dto.getDescription());
        assertEquals(5L, dto.getRequesterId());
        assertEquals(created, dto.getCreated());
    }
}