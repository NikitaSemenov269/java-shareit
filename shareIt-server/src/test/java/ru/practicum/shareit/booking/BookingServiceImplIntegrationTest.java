package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.enums.BookingStatus.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        // Arrange
        User owner = createUser("owner@mail.com", "Owner");
        User booker = createUser("booker@mail.com", "Booker");
        Item item = createItem("Item", "Description", true, owner);

        BookingRequestDto requestDto = new BookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        // Act
        BookingDto result = bookingService.createBooking(booker.getId(), requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(WAITING, result.getStatus());
    }

    @Test
    void createBooking_ShouldThrowException_WhenItemNotAvailable() {
        // Arrange
        User owner = createUser("owner2@mail.com", "Owner2");
        User booker = createUser("booker2@mail.com", "Booker2");
        Item item = createItem("Item2", "Description2", false, owner);

        BookingRequestDto requestDto = new BookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(booker.getId(), requestDto));
    }

    @Test
    void createBooking_ShouldThrowException_WhenOwnerBooksOwnItem() {
        // Arrange
        User owner = createUser("owner3@mail.com", "Owner3");
        Item item = createItem("Item3", "Description3", true, owner);

        BookingRequestDto requestDto = new BookingRequestDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(owner.getId(), requestDto));
    }

    @Test
    void updateAvailableStatusBooking_ShouldApproveBooking() {
        // Arrange
        User owner = createUser("owner4@mail.com", "Owner4");
        User booker = createUser("booker4@mail.com", "Booker4");
        Item item = createItem("Item4", "Description4", true, owner);
        Booking booking = createBooking(item, booker, WAITING);

        // Act
        BookingDto result = bookingService.updateAvailableStatusBooking(owner.getId(), booking.getId(), true);

        // Assert
        assertNotNull(result);
        assertEquals(APPROVED, result.getStatus());
    }

    @Test
    void updateAvailableStatusBooking_ShouldRejectBooking() {
        // Arrange
        User owner = createUser("owner5@mail.com", "Owner5");
        User booker = createUser("booker5@mail.com", "Booker5");
        Item item = createItem("Item5", "Description5", true, owner);
        Booking booking = createBooking(item, booker, WAITING);

        // Act
        BookingDto result = bookingService.updateAvailableStatusBooking(owner.getId(), booking.getId(), false);

        // Assert
        assertNotNull(result);
        assertEquals(REJECTED, result.getStatus());
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        // Arrange
        User owner = createUser("owner6@mail.com", "Owner6");
        User booker = createUser("booker6@mail.com", "Booker6");
        Item item = createItem("Item6", "Description6", true, owner);
        Booking booking = createBooking(item, booker, APPROVED);

        // Act
        BookingDto result = bookingService.getBookingById(booker.getId(), booking.getId());

        // Assert
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnBookings() {
        // Arrange
        User owner = createUser("owner7@mail.com", "Owner7");
        User booker = createUser("booker7@mail.com", "Booker7");
        Item item = createItem("Item7", "Description7", true, owner);
        createBooking(item, booker, APPROVED);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(booker.getId(), State.ALL);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByOwnerId_ShouldReturnBookings() {
        // Arrange
        User owner = createUser("owner8@mail.com", "Owner8");
        User booker = createUser("booker8@mail.com", "Booker8");
        Item item = createItem("Item8", "Description8", true, owner);
        createBooking(item, booker, APPROVED);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByOwnerId(owner.getId(), State.ALL);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void canceledBookingById_ShouldCancelBooking() {
        // Arrange
        User owner = createUser("owner9@mail.com", "Owner9");
        User booker = createUser("booker9@mail.com", "Booker9");
        Item item = createItem("Item9", "Description9", true, owner);
        Booking booking = createBooking(item, booker, APPROVED);

        // Act
        bookingService.canceledBookingById(booker.getId(), booking.getId());

        // Assert
        Booking canceledBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(CANCELED, canceledBooking.getStatus());
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(Item item, User booker, ru.practicum.enums.BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(status);
        booking.setItem(item);
        booking.setBooker(booker);
        return bookingRepository.save(booking);
    }
}