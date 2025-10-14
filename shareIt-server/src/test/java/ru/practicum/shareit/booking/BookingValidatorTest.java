package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingValidatorTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingValidator bookingValidator;

    @Test
    void existsByUserId_WithExistingUser_ShouldNotThrowException() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> bookingValidator.existsByUserId(userId));
    }

    @Test
    void existsByUserId_WithNonExistingUser_ShouldThrowNotFoundException() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingValidator.existsByUserId(userId));
    }

    @Test
    void bookingDateValidation_WithAvailableDates_ShouldNotThrowException() {
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        when(bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(itemId, start, end))
                .thenReturn(false);

        assertDoesNotThrow(() -> bookingValidator.bookingDateValidation(itemId, start, end));
    }

    @Test
    void bookingDateValidation_WithOccupiedDates_ShouldThrowValidationException() {
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        when(bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(itemId, start, end))
                .thenReturn(true);

        assertThrows(ValidationException.class, () ->
                bookingValidator.bookingDateValidation(itemId, start, end));
    }
}