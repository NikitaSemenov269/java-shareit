package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.*;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto;
    private ItemWithBookingAndCommentsDto itemWithBookingsDto;
    private CommentDto commentDto;
    private final Long userId = 1L;
    private final Long itemId = 1L;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Test Item");
        itemRequestDto.setDescription("Test Description");
        itemRequestDto.setAvailable(true);

        itemDto = new ItemDto(itemId, userId, "Test Item", "Test Description", true, null);
        itemWithBookingsDto = new ItemWithBookingAndCommentsDto();
        itemWithBookingsDto.setId(itemId);
        itemWithBookingsDto.setName("Test Item");

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createItem_WithValidData_ShouldReturnCreatedItem() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Item")));

        verify(itemService).createItem(eq(userId), any());
    }

    @Test
    void getItemById_WithValidData_ShouldReturnItem() throws Exception {
        when(itemService.getItemById(itemId, userId)).thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId.intValue())));

        verify(itemService).getItemById(itemId, userId);
    }

    @Test
    void searchAllItemOfOwnerById_ShouldReturnItems() throws Exception {
        Collection<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.searchAllItemOfOwnerById(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemService).searchAllItemOfOwnerById(userId);
    }

    @Test
    void searchItemDTOByText_WithText_ShouldReturnItems() throws Exception {
        Collection<ItemDto> items = Arrays.asList(itemDto);
        when(itemService.searchItemDtoByText("test")).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemService).searchItemDtoByText("test");
    }

    @Test
    void updateItem_WithValidData_ShouldReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(eq(itemId), eq(userId), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());

        verify(itemService).updateItem(itemId, userId, itemRequestDto);
    }

    @Test
    void deleteItem_WithValidData_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(itemService).deleteItem(userId, itemId);

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItem(userId, itemId);
    }

    @Test
    void addComment_WithValidData_ShouldReturnComment() throws Exception {
        when(itemService.addComment(userId, itemId, "Great item!")).thenReturn(commentDto);

        String commentJson = "{\"text\": \"Great item!\"}";

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Great item!")));

        verify(itemService).addComment(userId, itemId, "Great item!");
    }
}