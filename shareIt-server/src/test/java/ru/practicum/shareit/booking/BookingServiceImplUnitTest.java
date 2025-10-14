package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.shareit.booking.interfaces.BookingMapper;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
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
        // Given
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
        booking.setItem(item);
        booking.setBooker(booker);

        BookingDto expectedDto = new BookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(mapper.toBooking(requestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        // When
        BookingDto result = bookingService.createBooking(bookerId, requestDto);

        // Then
        assertNotNull(result);
        verify(bookingValidator).existsByUserId(bookerId);
        verify(bookingValidator).bookingDateValidation(eq(itemId), any(), any());
        verify(bookingRepository).save(booking);
    }

    @Test
    void getBookingById_WithValidUser_ShouldReturnBooking() {
        // Given
        Long userId = 1L;
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);

        BookingDto expectedDto = new BookingDto();

        when(bookingRepository.findByIdForAuthorOrOwner(bookingId, userId))
                .thenReturn(Optional.of(booking));
        when(mapper.toDto(booking)).thenReturn(expectedDto);

        // When
        BookingDto result = bookingService.getBookingById(userId, bookingId);

        // Then
        assertNotNull(result);
        verify(bookingValidator).existsByUserId(userId);
    }
}