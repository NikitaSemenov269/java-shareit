package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingMapper;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.enums.BookingStatus.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingValidator bookingValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingMapper mapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Long bookerId = 1L;
    private final Long ownerId = 2L;
    private final Long itemId = 1L;
    private final Long bookingId = 1L;

    @Test
    void createBooking_ShouldCreateBookingSuccessfully() {
        // Arrange
        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingRequestDto requestDto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        Booking booking = new Booking();
        booking.setId(bookingId);
        BookingDto expectedDto = new BookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(bookingValidator).existsByUserId(bookerId);
        doNothing().when(bookingValidator).bookingDateValidation(anyLong(), any(), any());
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(mapper.toBooking(requestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto result = bookingService.createBooking(bookerId, requestDto);

        // Assert
        assertNotNull(result);
        verify(bookingRepository).save(booking);
        verify(mapper).toDto(booking);
    }

    @Test
    void createBooking_ShouldThrowException_WhenItemNotFound() {
        // Arrange
        BookingRequestDto requestDto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        doNothing().when(bookingValidator).existsByUserId(bookerId);

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(bookerId, requestDto));
    }

    @Test
    void createBooking_ShouldThrowException_WhenItemNotAvailable() {
        // Arrange
        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(false);
        item.setOwner(owner);

        BookingRequestDto requestDto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(bookingValidator).existsByUserId(bookerId);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(bookerId, requestDto));
    }

    @Test
    void createBooking_ShouldThrowException_WhenOwnerBooksOwnItem() {
        // Arrange
        User owner = new User();
        owner.setId(bookerId);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingRequestDto requestDto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                WAITING
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(bookingValidator).existsByUserId(bookerId);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(bookerId, requestDto));
    }

    @Test
    void updateAvailableStatusBooking_ShouldApproveBooking() {
        // Arrange
        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(WAITING);

        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findByIdWithItemAndOwner(bookingId)).thenReturn(Optional.of(booking));
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto result = bookingService.updateAvailableStatusBooking(ownerId, bookingId, true);

        // Assert
        assertNotNull(result);
        assertEquals(APPROVED, booking.getStatus());
        verify(itemService).updateItemAvailable(item.getId(), false);
    }

    @Test
    void updateAvailableStatusBooking_ShouldRejectBooking() {
        // Arrange
        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setItem(item);
        booking.setStatus(WAITING);

        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findByIdWithItemAndOwner(bookingId)).thenReturn(Optional.of(booking));
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto result = bookingService.updateAvailableStatusBooking(ownerId, bookingId, false);

        // Assert
        assertNotNull(result);
        assertEquals(REJECTED, booking.getStatus());
        verify(itemService).updateItemAvailable(item.getId(), true);
    }

    @Test
    void updateAvailableStatusBooking_ShouldThrowException_WhenUserNotOwner() {
        // Arrange
        User owner = new User();
        owner.setId(999L);

        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setItem(item);

        when(bookingRepository.findByIdWithItemAndOwner(bookingId)).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                bookingService.updateAvailableStatusBooking(ownerId, bookingId, true));
    }

    @Test
    void canceledBookingById_ShouldCancelApprovedBooking() {
        // Arrange
        User booker = new User();
        booker.setId(bookerId);

        Item item = new Item();
        item.setId(itemId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(APPROVED);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findByIdAndBooker(bookingId, bookerId)).thenReturn(Optional.of(booking));

        // Act
        bookingService.canceledBookingById(bookerId, bookingId);

        // Assert
        assertEquals(CANCELED, booking.getStatus());
        verify(itemService).updateItemAvailable(itemId, true);
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        // Arrange
        Booking booking = new Booking();
        BookingDto expectedDto = new BookingDto();

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findByIdForAuthorOrOwner(bookingId, bookerId)).thenReturn(Optional.of(booking));
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        // Act
        BookingDto result = bookingService.getBookingById(bookerId, bookingId);

        // Assert
        assertNotNull(result);
        verify(mapper).toDto(booking);
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnAllBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findAllBookingByBookerId(bookerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(bookerId, State.ALL);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnCurrentBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findAllCurrentBookingByBookerId(bookerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(bookerId, State.CURRENT);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnPastBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findAllPastBookingByBookerId(bookerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(bookerId, State.PAST);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnFutureBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findAllFutureBookingByBookerId(bookerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(bookerId, State.FUTURE);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnWaitingBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findAllWaitingBookingByBookerId(bookerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(bookerId, State.WAITING);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnRejectedBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(bookerId);
        when(bookingRepository.findAllRejectedBookingByBookerId(bookerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByBookerId(bookerId, State.REJECTED);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByOwnerId_ShouldReturnAllBookings() {
        // Arrange
        BookingDto bookingDto = new BookingDto();
        Collection<BookingDto> expected = Arrays.asList(bookingDto);

        doNothing().when(bookingValidator).existsByUserId(ownerId);
        when(bookingRepository.findAllBookingByOwnerId(ownerId)).thenReturn(expected);

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByOwnerId(ownerId, State.ALL);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingByOwnerId_ShouldReturnEmpty_WhenNoBookings() {
        // Arrange
        doNothing().when(bookingValidator).existsByUserId(ownerId);
        when(bookingRepository.findAllBookingByOwnerId(ownerId)).thenReturn(Arrays.asList());

        // Act
        Collection<BookingDto> result = bookingService.getAllBookingByOwnerId(ownerId, State.ALL);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}