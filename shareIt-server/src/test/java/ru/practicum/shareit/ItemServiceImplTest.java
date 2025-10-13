package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.ItemRequestDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_AvailableIsNull_ThrowsException() {
        Long ownerId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test");
        requestDto.setAvailable(null);

        assertThrows(ValidationException.class, () -> {
            itemService.createItem(ownerId, requestDto);
        });

        verifyNoInteractions(itemMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void searchItemDtoByText_EmptyText_ReturnsEmptyList() {
        var result = itemService.searchItemDtoByText("   ");

        assertTrue(result.isEmpty());
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemMapper);
    }

    @Test
    void searchItemDtoByText_ValidText_CallsRepository() {
        String searchText = "item";
        when(itemRepository.findAllByText(searchText))
                .thenReturn(Collections.emptyList());

        var result = itemService.searchItemDtoByText(searchText);

        assertNotNull(result);
        verify(itemRepository).findAllByText(searchText);
    }
}