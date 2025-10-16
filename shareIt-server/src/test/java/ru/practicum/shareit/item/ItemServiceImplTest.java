package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.interfaces.*;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

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

    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long itemId = 1L;
    private final Long requestId = 1L;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@test.com");
        return user;
    }

    private Item createItem() {
        User owner = createUser(ownerId);
        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private Item createItemWithRequest() {
        Item item = createItem();
        Request request = new Request();
        request.setId(requestId);
        item.setRequest(request);
        return item;
    }

    @Test
    void createItem_ShouldCreateItemSuccessfully() {
        ItemRequestDto requestDto = new ItemRequestDto("Test Item", "Test Description", true, null);
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        ItemDto expectedDto = new ItemDto(itemId, userId, "Test Item", "Test Description", true, null);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(createUser(userId)));
        when(itemMapper.toItem(eq(requestDto))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(createItem());
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(expectedDto);

        ItemDto result = itemService.createItem(userId, requestDto);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Test Item", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_WithRequestId_ShouldCreateItemWithRequest() {
        ItemRequestDto requestDto = new ItemRequestDto("Test Item", "Test Description", true, requestId);
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        ItemDto expectedDto = new ItemDto(itemId, userId, "Test Item", "Test Description", true, requestId);
        Request request = new Request();
        request.setId(requestId);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(createUser(userId)));
        when(requestRepository.findById(eq(requestId))).thenReturn(Optional.of(request));
        when(itemMapper.toItem(eq(requestDto))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(createItemWithRequest());
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(expectedDto);

        ItemDto result = itemService.createItem(userId, requestDto);

        assertNotNull(result);
        assertEquals(requestId, result.getRequestId());
        verify(requestRepository).findById(eq(requestId));
    }

    @Test
    void createItem_WhenUserNotFound_ShouldThrowException() {
        ItemRequestDto requestDto = new ItemRequestDto("Test Item", "Test Description", true, null);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, requestDto));

        verify(itemMapper, never()).toItem(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_WhenAvailableIsNull_ShouldThrowException() {
        ItemRequestDto requestDto = new ItemRequestDto("Test", "Desc", null, null);

        assertThrows(ValidationException.class, () -> itemService.createItem(userId, requestDto));

        verify(userRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItem_WhenRequestNotFound_ShouldThrowException() {
        ItemRequestDto requestDto = new ItemRequestDto("Test Item", "Test Description", true, requestId);
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(createUser(userId)));
        when(requestRepository.findById(eq(requestId))).thenReturn(Optional.empty());
        when(itemMapper.toItem(eq(requestDto))).thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, requestDto));

        verify(itemRepository, never()).save(any());
    }

    // Тесты для updateItem
    @Test
    void updateItem_ShouldUpdateItemSuccessfully() {
        ItemRequestDto requestDto = new ItemRequestDto("Updated Item", "Updated Desc", false, null);
        Item existingItem = createItem();
        ItemDto expectedDto = new ItemDto(itemId, ownerId, "Updated Item", "Updated Desc", false, null);

        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        doNothing().when(itemValidation).itemValidationBelongsByIdOwner(eq(ownerId), eq(itemId));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        when(itemMapper.toItemDto(eq(existingItem))).thenReturn(expectedDto);

        ItemDto result = itemService.updateItem(itemId, ownerId, requestDto);

        assertNotNull(result);
        assertEquals("Updated Item", result.getName());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals(false, result.getAvailable());
    }

    @Test
    void updateItem_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("New Name");
        Item existingItem = createItem();
        ItemDto expectedDto = new ItemDto(itemId, ownerId, "New Name", "Test Description", true, null);

        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        doNothing().when(itemValidation).itemValidationBelongsByIdOwner(eq(ownerId), eq(itemId));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));
        when(itemMapper.toItemDto(eq(existingItem))).thenReturn(expectedDto);

        ItemDto result = itemService.updateItem(itemId, ownerId, requestDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(true, result.getAvailable());
    }

    @Test
    void updateItem_WhenItemNotFound_ShouldThrowException() {
        ItemRequestDto requestDto = new ItemRequestDto("Updated", "Desc", true, null);

        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        doNothing().when(itemValidation).itemValidationBelongsByIdOwner(eq(ownerId), eq(itemId));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemId, ownerId, requestDto));
    }

    @Test
    void deleteItem_ShouldDeleteItemSuccessfully() {
        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        doNothing().when(itemValidation).itemValidationBelongsByIdOwner(eq(ownerId), eq(itemId));

        itemService.deleteItem(ownerId, itemId);

        verify(itemRepository).deleteById(eq(itemId));
    }

    @Test
    void getItemById_ShouldReturnItemWithBookingsForOwner() {
        Item item = createItem();
        ItemWithBookingAndCommentsDto expectedDto = new ItemWithBookingAndCommentsDto();
        expectedDto.setId(itemId);
        expectedDto.setName("Test Item");
        expectedDto.setAvailable(true);
        LastBookingDto lastBooking = new LastBookingDto(1L, LocalDateTime.now(), LocalDateTime.now());
        NextBookingDto nextBooking = new NextBookingDto(2L, LocalDateTime.now(), LocalDateTime.now());

        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        when(itemMapper.itemDtoWithBookingAndComments(eq(item))).thenReturn(expectedDto);
        when(commentRepository.findCommentByItemId(eq(itemId))).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingDto(eq(itemId), any(LocalDateTime.class))).thenReturn(lastBooking);
        when(bookingRepository.findNextBookingDto(eq(itemId), any(LocalDateTime.class))).thenReturn(nextBooking);

        ItemWithBookingAndCommentsDto result = itemService.getItemById(itemId, ownerId);

        assertNotNull(result);
        verify(bookingRepository).findLastBookingDto(eq(itemId), any(LocalDateTime.class));
        verify(bookingRepository).findNextBookingDto(eq(itemId), any(LocalDateTime.class));
    }

    @Test
    void getItemById_ShouldReturnItemWithoutBookingsForNonOwner() {
        Item item = createItem();
        ItemWithBookingAndCommentsDto expectedDto = new ItemWithBookingAndCommentsDto();
        expectedDto.setId(itemId);
        expectedDto.setName("Test Item");

        doNothing().when(itemValidation).existsByUserId(eq(userId));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        when(itemMapper.itemDtoWithBookingAndComments(eq(item))).thenReturn(expectedDto);
        when(commentRepository.findCommentByItemId(eq(itemId))).thenReturn(Collections.emptyList());

        ItemWithBookingAndCommentsDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        verify(bookingRepository, never()).findLastBookingDto(anyLong(), any());
        verify(bookingRepository, never()).findNextBookingDto(anyLong(), any());
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldThrowException() {
        doNothing().when(itemValidation).existsByUserId(eq(userId));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    void searchItemDtoByText_ShouldReturnItems() {
        ItemDto itemDto = new ItemDto(itemId, ownerId, "Test Item", "Test Description", true, null);
        List<ItemDto> expectedItems = List.of(itemDto);

        when(itemRepository.findAllByText(eq("test"))).thenReturn(expectedItems);

        Collection<ItemDto> result = itemService.searchItemDtoByText("test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void searchItemDtoByText_WithEmptyText_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.searchItemDtoByText("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findAllByText(anyString());
    }

    @Test
    void searchItemDtoByText_WithNullText_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.searchItemDtoByText(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemDtoByText_WithWhitespaceText_ShouldReturnEmptyList() {
        Collection<ItemDto> result = itemService.searchItemDtoByText("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAllItemOfOwnerById_ShouldReturnItems() {
        Item item = createItem();
        ItemDto itemDto = new ItemDto(itemId, ownerId, "Test Item", "Test Description", true, null);

        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        when(itemRepository.findByOwnerId(eq(ownerId))).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        Collection<ItemDto> result = itemService.searchAllItemOfOwnerById(ownerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void searchAllItemOfOwnerById_WhenNoItems_ShouldReturnEmptyList() {
        doNothing().when(itemValidation).existsByUserId(eq(ownerId));
        when(itemRepository.findByOwnerId(eq(ownerId))).thenReturn(Collections.emptyList());

        Collection<ItemDto> result = itemService.searchAllItemOfOwnerById(ownerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateItemAvailable_ShouldUpdateAvailability() {
        Item item = createItem();
        Boolean newAvailability = false;

        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));
        when(itemRepository.save(eq(item))).thenReturn(item);

        itemService.updateItemAvailable(itemId, newAvailability);

        assertEquals(newAvailability, item.getAvailable());
        verify(itemRepository).save(eq(item));
    }

    @Test
    void updateItemAvailable_WhenStatusSame_ShouldNotSave() {
        Item item = createItem();
        Boolean sameAvailability = true;

        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(item));

        itemService.updateItemAvailable(itemId, sameAvailability);

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemAvailable_WhenItemNotFound_ShouldThrowException() {
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItemAvailable(itemId, true));
    }

    @Test
    void updateItemAvailable_WhenStatusNull_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> itemService.updateItemAvailable(itemId, null));
    }

    @Test
    void addComment_ShouldAddCommentSuccessfully() {
        String commentText = "Great item!";
        Comment comment = new Comment();
        comment.setText(commentText);
        CommentDto expectedDto = new CommentDto(1L, commentText, "Test User", LocalDateTime.now());
        when(bookingRepository.existsCompletedBookingByUserAndItem(eq(userId), eq(itemId))).thenReturn(true);
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(createUser(userId)));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(createItem()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(eq(comment))).thenReturn(expectedDto);

        CommentDto result = itemService.addComment(userId, itemId, commentText);

        assertNotNull(result);
        assertEquals(commentText, result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_WhenUserNotBookedItem_ShouldThrowException() {
        String commentText = "Great item!";

        when(bookingRepository.existsCompletedBookingByUserAndItem(eq(userId), eq(itemId))).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void addComment_WhenUserNotFound_ShouldThrowException() {
        String commentText = "Great item!";

        when(bookingRepository.existsCompletedBookingByUserAndItem(eq(userId), eq(itemId))).thenReturn(true);
        when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void addComment_WhenItemNotFound_ShouldThrowException() {
        String commentText = "Great item!";

        when(bookingRepository.existsCompletedBookingByUserAndItem(eq(userId), eq(itemId))).thenReturn(true);
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(createUser(userId)));
        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(userId, itemId, commentText));
    }

    @Test
    void addComment_WithEmptyComment_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, ""));
    }

    @Test
    void addComment_WithBlankComment_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, "   "));
    }

    @Test
    void addComment_WithNullComment_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, null));
    }
}