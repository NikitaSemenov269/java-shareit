package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.*;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired private ItemService itemService;
    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private RequestRepository requestRepository;

    private User owner, booker, requester;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();

        owner = createUser("owner-item@test.com", "Item Owner");
        booker = createUser("booker-item@test.com", "Item Booker");
        requester = createUser("requester-item@test.com", "Item Requester");
        item = createItem("Test Item", "Test Description", owner, true);
    }

    @Test
    void createItem_WithValidData_ShouldCreateItem() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("New Item");
        requestDto.setDescription("New Description");
        requestDto.setAvailable(true);

        ItemDto result = itemService.createItem(owner.getId(), requestDto);

        assertNotNull(result);
        assertEquals("New Item", result.getName());
        assertEquals(owner.getId(), result.getOwnerId());
    }

    @Test
    void createItem_WithRequest_ShouldCreateItemWithRequest() {
        Request request = createRequest(requester);
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Item for Request");
        requestDto.setDescription("Description");
        requestDto.setAvailable(true);
        requestDto.setRequestId(request.getId());

        ItemDto result = itemService.createItem(owner.getId(), requestDto);

        assertEquals(request.getId(), result.getRequestId());
    }

    @Test
    void createItem_WithoutAvailable_ShouldThrowException() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Test Item");
        requestDto.setDescription("Test Description");

        assertThrows(ValidationException.class, () ->
                itemService.createItem(owner.getId(), requestDto));
    }

    @Test
    void updateItem_WithValidData_ShouldUpdateItem() {
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");
        updateDto.setAvailable(false);

        ItemDto result = itemService.updateItem(item.getId(), owner.getId(), updateDto);

        assertEquals("Updated Name", result.getName());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_WithNonOwner_ShouldThrowException() {
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setName("Updated Name");

        assertThrows(ValidationException.class, () ->
                itemService.updateItem(item.getId(), booker.getId(), updateDto));
    }

    @Test
    void getItemById_ForOwner_ShouldReturnItemWithBookings() {
        createBooking(item, booker, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);

        ItemWithBookingAndCommentsDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void getItemById_ForNonOwner_ShouldReturnItemWithoutBookings() {
        createBooking(item, booker, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);

        ItemWithBookingAndCommentsDto result = itemService.getItemById(item.getId(), booker.getId());

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void searchItemDtoByText_WithMatchingText_ShouldReturnItems() {
        createItem("Laptop", "Gaming laptop", owner, true);

        Collection<ItemDto> result = itemService.searchItemDtoByText("laptop");

        assertThat(result).isNotEmpty();
    }

    @Test
    void searchItemDtoByText_WithEmptyText_ShouldReturnEmpty() {
        Collection<ItemDto> result = itemService.searchItemDtoByText("");

        assertThat(result).isEmpty();
    }

    @Test
    void searchAllItemOfOwnerById_ShouldReturnOwnerItems() {
        createItem("Second Item", "Another item", owner, true);

        Collection<ItemDto> result = itemService.searchAllItemOfOwnerById(owner.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void addComment_WithValidBooking_ShouldCreateComment() {
        createBooking(item, booker, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), "Great item!");

        assertEquals("Great item!", result.getText());
        assertEquals("Item Booker", result.getAuthorName());
    }

    @Test
    void addComment_WithoutBooking_ShouldThrowException() {
        assertThrows(ValidationException.class, () ->
                itemService.addComment(booker.getId(), item.getId(), "Comment without booking"));
    }

    @Test
    void deleteItem_WithValidOwner_ShouldDeleteItem() {
        itemService.deleteItem(owner.getId(), item.getId());

        assertFalse(itemRepository.existsById(item.getId()));
    }

    @Test
    void updateItemAvailable_WithValidData_ShouldUpdateAvailability() {
        itemService.updateItemAvailable(item.getId(), false);

        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertFalse(updatedItem.getAvailable());
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, User owner, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    private Request createRequest(User requester) {
        Request request = new Request();
        request.setDescription("Need item for testing");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }
}