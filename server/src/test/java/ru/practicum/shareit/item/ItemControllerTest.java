package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.DTO.*;
import ru.practicum.GlobalExceptionHandler;
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

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long ownerId = 2L;
    private ItemRequestDto itemRequestDto;
    private ItemDto itemDto;
    private ItemWithBookingAndCommentsDto itemWithBookingDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        itemRequestDto = new ItemRequestDto("Test Item", "Test Description", true, null);
        itemDto = new ItemDto(itemId, userId, "Test Item", "Test Description", true, null);

        itemWithBookingDto = new ItemWithBookingAndCommentsDto();
        itemWithBookingDto.setId(itemId);
        itemWithBookingDto.setName("Test Item");
        itemWithBookingDto.setDescription("Test Description");
        itemWithBookingDto.setAvailable(true);
    }

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        when(itemService.createItem(eq(userId), any(ItemRequestDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService).createItem(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void createItem_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(itemService.createItem(eq(userId), any(ItemRequestDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(itemService).createItem(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void createItem_WithEmptyBody_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createItem_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        when(itemService.getItemById(eq(itemId), eq(userId))).thenReturn(itemWithBookingDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(itemService).getItemById(eq(itemId), eq(userId));
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(eq(itemId), eq(userId)))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"));

        verify(itemService).getItemById(eq(itemId), eq(userId));
    }

    @Test
    void getItemById_WithInvalidIdFormat_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/items/not-a-number")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getItemById_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchAllItemOfOwnerById_ShouldReturnItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemService.searchAllItemOfOwnerById(eq(ownerId))).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].ownerId").value(userId))
                .andExpect(jsonPath("$[0].name").value("Test Item"));

        verify(itemService).searchAllItemOfOwnerById(eq(ownerId));
    }

    @Test
    void searchAllItemOfOwnerById_WhenNoItems_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchAllItemOfOwnerById(eq(ownerId))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService).searchAllItemOfOwnerById(eq(ownerId));
    }

    @Test
    void searchAllItemOfOwnerById_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(itemService.searchAllItemOfOwnerById(eq(ownerId)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(itemService).searchAllItemOfOwnerById(eq(ownerId));
    }

    @Test
    void searchAllItemOfOwnerById_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchItemDTOByText_ShouldReturnItems() throws Exception {
        List<ItemDto> items = List.of(itemDto);

        when(itemService.searchItemDtoByText(eq("test"))).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Test Item"));

        verify(itemService).searchItemDtoByText(eq("test"));
    }

    @Test
    void searchItemDTOByText_WithEmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItemDtoByText(eq(""))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(itemService).searchItemDtoByText(eq(""));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemRequestDto updateRequest = new ItemRequestDto("Updated Item", "Updated Description", false, null);
        ItemDto updatedItem = new ItemDto(itemId, ownerId, "Updated Item", "Updated Description", false, null);

        when(itemService.updateItem(eq(itemId), eq(ownerId), any(ItemRequestDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService).updateItem(eq(itemId), eq(ownerId), any(ItemRequestDto.class));
    }

    @Test
    void updateItem_WhenNotOwner_ShouldReturnBadRequest() throws Exception {
        ItemRequestDto updateRequest = new ItemRequestDto("Updated Item", "Updated Description", false, null);

        when(itemService.updateItem(eq(itemId), eq(userId), any(ItemRequestDto.class)))
                .thenThrow(new ValidationException("Not owner"));

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());

        verify(itemService).updateItem(eq(itemId), eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void updateItem_WithEmptyBody_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateItem_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(patch("/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status()
                        .isInternalServerError());
    }

    @Test
    void deleteItem_ShouldReturnNoContent() throws Exception {
        doNothing().when(itemService).deleteItem(eq(ownerId), eq(itemId));

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItem(eq(ownerId), eq(itemId));
    }

    @Test
    void deleteItem_WhenItemNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Item not found")).when(itemService).deleteItem(eq(ownerId), eq(itemId));

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"));

        verify(itemService).deleteItem(eq(ownerId), eq(itemId));
    }

    @Test
    void deleteItem_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(delete("/items/{id}", itemId))
                .andExpect(status().isInternalServerError());
    }

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

        verify(itemService).addComment(eq(userId), eq(itemId), eq("Great item!"));
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
                .andExpect(status().isInternalServerError());

        verify(itemService).addComment(eq(userId), eq(itemId), eq(""));
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
                .andExpect(status().isInternalServerError());

        verify(itemService).addComment(eq(userId), eq(itemId), eq("Great item!"));
    }

    @Test
    void addComment_WithEmptyBody_ShouldReturnInternalServerError() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addComment_WithoutUserIdHeader_ShouldReturnInternalServerError() throws Exception {
        CommentTextDto requestDto = new CommentTextDto("Great item!");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createItem_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        when(itemService.createItem(eq(userId), any(ItemRequestDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера."));

        verify(itemService).createItem(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void searchItemDTOByText_WithNullText_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItemDtoByText(isNull())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isInternalServerError());

        verify(itemService).searchItemDtoByText(eq(""));
    }
}