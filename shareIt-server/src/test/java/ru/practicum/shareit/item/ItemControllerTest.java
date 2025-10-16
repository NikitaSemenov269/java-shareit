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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long ownerId = 2L;

    // Тест создания предмета
    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Test Item", "Test Description", true, null);
        ItemDto responseDto = new ItemDto(itemId, userId, "Test Item", "Test Description", true, null);

        when(itemService.createItem(eq(userId), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createItem_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Test Item", "Test Description", true, null);

        when(itemService.createItem(eq(userId), any(ItemRequestDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    // Тест получения предмета по ID
    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        ItemWithBookingAndCommentsDto responseDto = new ItemWithBookingAndCommentsDto();
        responseDto.setId(itemId);
        responseDto.setName("Test Item");
        responseDto.setDescription("Test Description");
        responseDto.setAvailable(true);

        when(itemService.getItemById(eq(itemId), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(eq(itemId), eq(userId)))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    // Тест получения всех предметов владельца
    @Test
    void searchAllItemOfOwnerById_ShouldReturnItems() throws Exception {
        ItemDto itemDto = new ItemDto(itemId, ownerId, "Test Item", "Test Description", true, null);
        List<ItemDto> items = List.of(itemDto);

        when(itemService.searchAllItemOfOwnerById(eq(ownerId))).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].ownerId").value(ownerId));
    }

    @Test
    void searchAllItemOfOwnerById_WhenNoItems_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchAllItemOfOwnerById(eq(ownerId))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Тест поиска предметов по тексту
    @Test
    void searchItemDTOByText_ShouldReturnItems() throws Exception {
        ItemDto itemDto = new ItemDto(itemId, ownerId, "Test Item", "Test Description", true, null);
        List<ItemDto> items = List.of(itemDto);

        when(itemService.searchItemDtoByText(eq("test"))).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void searchItemDTOByText_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItemDtoByText(eq(""))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Тест обновления предмета
    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Updated Item", "Updated Description", false, null);
        ItemDto responseDto = new ItemDto(itemId, ownerId, "Updated Item", "Updated Description", false, null);

        when(itemService.updateItem(eq(itemId), eq(ownerId), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void updateItem_WhenNotOwner_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto("Updated Item", "Updated Description", false, null);

        when(itemService.updateItem(eq(itemId), eq(userId), any(ItemRequestDto.class)))
                .thenThrow(new ValidationException("Not owner"));

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // Тест удаления предмета
    @Test
    void deleteItem_ShouldReturnNoContent() throws Exception {
        doNothing().when(itemService).deleteItem(eq(ownerId), eq(itemId));

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isNoContent());
    }

    // Тест добавления комментария
    @Test
    void addComment_ShouldReturnComment() throws Exception {
        CommentTextDto requestDto = new CommentTextDto("Great item!");
        CommentDto responseDto = new CommentDto(1L, "Great item!", "Test User", LocalDateTime.now());

        when(itemService.addComment(eq(userId), eq(itemId), eq("Great item!"))).thenReturn(responseDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("Test User"));
    }

    @Test
    void addComment_WithEmptyComment_ShouldReturnBadRequest() throws Exception {
        CommentTextDto requestDto = new CommentTextDto("");

        when(itemService.addComment(eq(userId), eq(itemId), eq("")))
                .thenThrow(new ValidationException("Comment cannot be empty"));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_WhenUserNotBookedItem_ShouldReturnBadRequest() throws Exception {
        CommentTextDto requestDto = new CommentTextDto("Great item!");

        when(itemService.addComment(eq(userId), eq(itemId), eq("Great item!")))
                .thenThrow(new ValidationException("User didn't book this item"));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}