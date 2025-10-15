/*
package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.*;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.interfaces.CommentMapper;
import ru.practicum.shareit.item.interfaces.CommentRepository;
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @Mock
    private ItemValidation itemValidation;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemById_WithExistingItem_ShouldReturnItem() {
        Long itemId = 1L;
        Long userId = 1L;

        Item item = new Item();
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        ItemWithBookingAndCommentsDto expectedDto = new ItemWithBookingAndCommentsDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.itemDtoWithBookingAndComments(item)).thenReturn(expectedDto);
        when(commentRepository.findCommentByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemWithBookingAndCommentsDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        verify(itemValidation).existsByUserId(userId);
    }

    @Test
    void searchItemDtoByText_WithEmptyText_ShouldReturnEmpty() {
        Collection<ItemDto> result = itemService.searchItemDtoByText("");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemDtoByText_WithValidText_ShouldReturnItems() {
        String searchText = "test";
        when(itemRepository.findAllByText(searchText)).thenReturn(Collections.emptyList());

        Collection<ItemDto> result = itemService.searchItemDtoByText(searchText);

        assertNotNull(result);
    }

    @Test
    void addComment_WithValidData_ShouldCreateComment() {
        Long userId = 1L;
        Long itemId = 1L;
        String commentText = "Great item!";

        when(bookingRepository.existsCompletedBookingByUserAndItem(userId, itemId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(commentMapper.toCommentDto(any())).thenReturn(new CommentDto());

        CommentDto result = itemService.addComment(userId, itemId, commentText);

        assertNotNull(result);
        verify(commentRepository).save(any());
    }

    @Test
    void addComment_WithoutCompletedBooking_ShouldThrowException() {
        Long userId = 1L;
        Long itemId = 1L;

        when(bookingRepository.existsCompletedBookingByUserAndItem(userId, itemId)).thenReturn(false);

        assertThrows(ValidationException.class, () ->
                itemService.addComment(userId, itemId, "Test comment"));
    }

    @Test
    void updateItemAvailable_WithValidData_ShouldUpdateAvailability() {
        Long itemId = 1L;
        Item item = new Item();
        item.setAvailable(true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.updateItemAvailable(itemId, false);

        verify(itemRepository).save(item);
    }
}*/
