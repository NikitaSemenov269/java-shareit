package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.DTO.ItemDtoForRequester;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void requestDto_Serialization_ShouldIncludeDescription() throws Exception {
        RequestDto dto = new RequestDto();
        dto.setDescription("Need item for testing");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isEqualTo("{\"description\":\"Need item for testing\"}");
    }

    @Test
    void requestDto_Deserialization_ShouldCreateValidObject() throws Exception {
        String json = "{\"description\":\"Need item for testing\"}";

        RequestDto dto = objectMapper.readValue(json, RequestDto.class);

        assertThat(dto.getDescription()).isEqualTo("Need item for testing");
    }

    @Test
    void responseRequestDto_Serialization_ShouldIncludeAllFields() throws Exception {
        ItemDtoForRequester itemDto = new ItemDtoForRequester(1L, "Test Item", 2L);
        ResponseRequestDto dto = new ResponseRequestDto(
                1L, 2L, "Need item", LocalDateTime.of(2024, 1, 1, 10, 0),
                Arrays.asList(itemDto)
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"requesterId\":2");
        assertThat(json).contains("\"description\":\"Need item\"");
        assertThat(json).contains("\"created\"");
        assertThat(json).contains("\"items\"");
    }

    @Test
    void responseRequestDto_Deserialization_ShouldCreateValidObject() throws Exception {
        String json = "{\"id\":1,\"requesterId\":2,\"description\":\"Need item\",\"created\":\"2024-01-01T10:00:00\",\"items\":[{\"id\":1,\"name\":\"Test Item\",\"ownerId\":2}]}";

        ResponseRequestDto dto = objectMapper.readValue(json, ResponseRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRequesterId()).isEqualTo(2L);
        assertThat(dto.getDescription()).isEqualTo("Need item");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().iterator().next().getName()).isEqualTo("Test Item");
    }

    @Test
    void responseRequestDto_WithEmptyItems_ShouldSerializeCorrectly() throws Exception {
        ResponseRequestDto dto = new ResponseRequestDto(1L, 2L, "Need item",
                LocalDateTime.now(), Arrays.asList());

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"items\":[]");
    }
}