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
import ru.practicum.shareit.booking.interfaces.BookingMapper;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void createBooking_WithValidData_ShouldCreateBooking() {
        Long bookerId = 2L;
        Long itemId = 1L;

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(itemId);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(bookerId);

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);

        BookingDto expectedDto = new BookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(mapper.toBooking(requestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        BookingDto result = bookingService.createBooking(bookerId, requestDto);

        assertNotNull(result);
        verify(bookingValidator).existsByUserId(bookerId);
        verify(bookingRepository).save(booking);
    }

    @Test
    void getAllBookingByBookerId_WithStateAll_ShouldReturnAllBookings() {
        Long bookerId = 1L;
        when(bookingRepository.findAllBookingByBookerId(bookerId))
                .thenReturn(Collections.emptyList());

        var result = bookingService.getAllBookingByBookerId(bookerId, State.ALL);

        assertNotNull(result);
        verify(bookingValidator).existsByUserId(bookerId);
    }

    @Test
    void getAllBookingByOwnerId_WithStateCurrent_ShouldReturnCurrentBookings() {
        Long ownerId = 1L;
        when(bookingRepository.findAllCurrentBookingByOwnerId(ownerId))
                .thenReturn(Collections.emptyList());

        var result = bookingService.getAllBookingByOwnerId(ownerId, State.CURRENT);

        assertNotNull(result);
        verify(bookingValidator).existsByUserId(ownerId);
    }

    @Test
    void getBookingById_WithValidUser_ShouldReturnBooking() {
        Long userId = 1L;
        Long bookingId = 1L;

        Booking booking = new Booking();
        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findByIdForAuthorOrOwner(bookingId, userId))
                .thenReturn(Optional.of(booking));
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        BookingDto result = bookingService.getBookingById(userId, bookingId);

        assertNotNull(result);
        verify(bookingValidator).existsByUserId(userId);
    }

    @Test
    void getBookingById_WithNonExistingBooking_ShouldThrowException() {
        Long userId = 1L;
        Long bookingId = 999L;

        when(bookingRepository.findByIdForAuthorOrOwner(bookingId, userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(userId, bookingId));
    }
}