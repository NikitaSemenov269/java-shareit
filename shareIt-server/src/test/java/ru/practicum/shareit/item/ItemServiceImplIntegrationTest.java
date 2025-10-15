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
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemMapper mapper;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = createUser("owner@test.com", "Owner");
        booker = createUser("booker@test.com", "Booker");
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
    void getItemById_ForOwner_ShouldReturnItemWithBookings() {
        createCompletedBooking();

        ItemWithBookingAndCommentsDto result = itemService.getItemById(item.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertNotNull(result.getLastBooking());
    }

    @Test
    void getItemById_ForNonOwner_ShouldReturnItemWithoutBookings() {
        createCompletedBooking();

        ItemWithBookingAndCommentsDto result = itemService.getItemById(item.getId(), booker.getId());

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void searchItemDtoByText_WithMatchingText_ShouldReturnItems() {
        Collection<ItemDto> result = itemService.searchItemDtoByText("test");

        assertFalse(result.isEmpty());
    }

    @Test
    void searchItemDtoByText_WithEmptyText_ShouldReturnEmpty() {
        Collection<ItemDto> result = itemService.searchItemDtoByText("");

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_WithValidCompletedBooking_ShouldCreateComment() {
        createCompletedBooking();

        CommentDto result = itemService.addComment(booker.getId(), item.getId(), "Great item!");

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
    }

    @Test
    void addComment_WithoutCompletedBooking_ShouldThrowException() {
        assertThrows(ValidationException.class, () ->
                itemService.addComment(booker.getId(), item.getId(), "Test comment"));
    }

    @Test
    void updateItem_WithValidData_ShouldUpdateItem() {
        ItemRequestDto updateDto = new ItemRequestDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");

        ItemDto result = itemService.updateItem(item.getId(), owner.getId(), updateDto);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    void deleteItem_WithValidOwner_ShouldDeleteItem() {
        assertDoesNotThrow(() -> itemService.deleteItem(owner.getId(), item.getId()));
    }

    private void createCompletedBooking() {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, User owner, boolean available) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName(name);
        requestDto.setDescription(description);
        requestDto.setAvailable(available);
        ItemDto itemDto = itemService.createItem(owner.getId(), requestDto);

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setAvailable(itemDto.getAvailable());

        return item;
    }
}