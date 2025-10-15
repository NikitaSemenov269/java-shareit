/*
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.DTO.CommentDto;
import ru.practicum.DTO.ItemDto;
import ru.practicum.DTO.ItemDtoForRequester;
import ru.practicum.DTO.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itemRequestDto_Serialization_ShouldIncludeAllFields() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"Test Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":1");
    }

    @Test
    void itemRequestDto_Deserialization_ShouldCreateValidObject() throws Exception {
        String json = "{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":1}";

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(1L);
    }

    @Test
    void itemDto_Serialization_ShouldIncludeAllFields() throws Exception {
        ItemDto dto = new ItemDto(1L, 2L, "Test Item", "Test Description", true, 3L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"ownerId\":2");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"Test Description\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":3");
    }

    @Test
    void itemDto_Deserialization_ShouldCreateValidObject() throws Exception {
        String json = "{\"id\":1,\"ownerId\":2,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":3}";

        ItemDto dto = objectMapper.readValue(json, ItemDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getOwnerId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getDescription()).isEqualTo("Test Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(3L);
    }

    @Test
    void itemDtoForRequester_Serialization_ShouldIncludeRequiredFields() throws Exception {
        ItemDtoForRequester dto = new ItemDtoForRequester(1L, "Test Item", 2L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"ownerId\":2");
    }

    @Test
    void itemDtoForRequester_Deserialization_ShouldCreateValidObject() throws Exception {
        String json = "{\"id\":1,\"name\":\"Test Item\",\"ownerId\":2}";

        ItemDtoForRequester dto = objectMapper.readValue(json, ItemDtoForRequester.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Test Item");
        assertThat(dto.getOwnerId()).isEqualTo(2L);
    }

    @Test
    void commentDto_Serialization_ShouldIncludeAllFields() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Test comment");
        dto.setAuthorName("Test User");
        dto.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).
                contains("\"text\":\"Test comment\"");
        assertThat(json).contains("\"authorName\":\"Test User\"");
        assertThat(json).contains("\"created\"");
    }

    @Test
    void commentDto_Deserialization_ShouldCreateValidObject() throws Exception {
        String json = "{\"id\":1,\"text\":\"Test comment\",\"authorName\":\"Test User\",\"created\":\"2024-01-01T10:00:00\"}";

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Test comment");
        assertThat(dto.getAuthorName()).isEqualTo("Test User");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
    }

    @Test
    void itemRequestDto_WithNullFields_ShouldSerializeCorrectly() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setName("Test Item");
        dto.setAvailable(true);
        // description and requestId are null

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"description\":null");
        assertThat(json).contains("\"requestId\":null");
    }

    @Test
    void itemDto_WithNullRequestId_ShouldSerializeCorrectly() throws Exception {
        ItemDto dto = new ItemDto(1L, 2L, "Test Item", "Test Description", true, null);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"requestId\":null");
    }
}*/
