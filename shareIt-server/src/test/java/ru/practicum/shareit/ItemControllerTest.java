package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.ItemDto;
import ru.practicum.DTO.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void getItemById_Success() throws Exception {
        ItemWithBookingAndCommentsDto itemDto = new ItemWithBookingAndCommentsDto();
        itemDto.setId(1L);
        itemDto.setName("Item");

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void searchAllItemOfOwnerById_Success() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(itemService.searchAllItemOfOwnerById(anyLong()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchItemDTOByText_Success() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(itemService.searchItemDtoByText(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}