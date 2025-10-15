/*
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto invalidRequest = new ItemRequestDto();
        // Missing required fields

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_WithNonExistingItem_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItemDTOByText_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItemDtoByText(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addComment_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        CommentTextDto invalidComment = new CommentTextDto();
        invalidComment.setText(""); // Empty text

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_WithNonOwner_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto updateRequest = createValidItemRequest();
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenThrow(new ValidationException("User is not owner"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    private ItemRequestDto createValidItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");
        requestDto.setAvailable(true);
        return requestDto;
    }
}*/
