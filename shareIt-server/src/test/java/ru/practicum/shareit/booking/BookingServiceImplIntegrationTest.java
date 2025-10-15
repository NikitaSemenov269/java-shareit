package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.State;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

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
    void createBooking_WithValidData_ShouldCreateBooking() {
        BookingRequestDto requestDto = createValidBookingRequest();

        BookingDto result = bookingService.createBooking(booker.getId(), requestDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void createBooking_WithUnavailableItem_ShouldThrowException() {
        Item unavailableItem = createItem("Unavailable Item", "Test", owner, false);
        BookingRequestDto requestDto = createValidBookingRequest();
        requestDto.setItemId(unavailableItem.getId());

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(booker.getId(), requestDto));
    }

    @Test
    void createBooking_ByOwner_ShouldThrowException() {
        BookingRequestDto requestDto = createValidBookingRequest();

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(owner.getId(), requestDto));
    }

    @Test
    void getAllBookingByBookerId_WithDifferentStates_ShouldReturnBookings() {
        createTestBooking();
        createTestBooking();

        Collection<BookingDto> allBookings = bookingService.getAllBookingByBookerId(booker.getId(), State.ALL);
        Collection<BookingDto> futureBookings = bookingService.getAllBookingByBookerId(booker.getId(), State.FUTURE);

        assertFalse(allBookings.isEmpty());
        assertFalse(futureBookings.isEmpty());
    }

    @Test
    void updateAvailableStatusBooking_ApproveBooking_ShouldUpdateStatus() {
        BookingRequestDto requestDto = createValidBookingRequest();
        BookingDto created = bookingService.createBooking(booker.getId(), requestDto);

        BookingDto result = bookingService.updateAvailableStatusBooking(owner.getId(), created.getId(), true);

        assertEquals(ru.practicum.enums.BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void getBookingById_WithValidUser_ShouldReturnBooking() {
        BookingRequestDto requestDto = createValidBookingRequest();
        BookingDto created = bookingService.createBooking(booker.getId(), requestDto);

        BookingDto result = bookingService.getBookingById(booker.getId(), created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
    }

    @Test
    void canceledBookingById_WithValidData_ShouldCancelBooking() {
        BookingRequestDto requestDto = createValidBookingRequest();
        BookingDto created = bookingService.createBooking(booker.getId(), requestDto);

        assertDoesNotThrow(() -> bookingService.canceledBookingById(booker.getId(), created.getId()));
    }

    private BookingRequestDto createValidBookingRequest() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));
        return requestDto;
    }

    private void createTestBooking() {
        BookingRequestDto requestDto = createValidBookingRequest();
        try {
            bookingService.createBooking(booker.getId(), requestDto);
        } catch (ValidationException e) {
            // Ignore date conflicts
        }
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
}