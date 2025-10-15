/*
package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMapperTest {

    private final RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Test
    void toDto_WithValidRequest_ShouldMapCorrectly() {
        User requester = new User();
        requester.setId(1L);

        Request request = new Request();
        request.setId(1L);
        request.setDescription("Need item for testing");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        ResponseRequestDto dto = requestMapper.toDto(request);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need item for testing");
        assertThat(dto.getRequesterId()).isEqualTo(1L);
    }
}*/
