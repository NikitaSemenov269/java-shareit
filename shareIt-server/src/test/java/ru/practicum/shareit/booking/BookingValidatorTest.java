package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
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
    void bookingDateValidation_ShouldThrowException_WhenDatesOverlap() {
        // Arrange
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(itemId, start, end))
                .thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                bookingValidator.bookingDateValidation(itemId, start, end));
    }

    @Test
    void bookingDateValidation_ShouldNotThrow_WhenNoOverlap() {
        // Arrange
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(itemId, start, end))
                .thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> bookingValidator.bookingDateValidation(itemId, start, end));
    }

    @Test
    void existsByUserId_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookingValidator.existsByUserId(userId));
    }

    @Test
    void existsByUserId_ShouldNotThrow_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> bookingValidator.existsByUserId(userId));
    }

    @Test
    void existsByItemId_ShouldThrowException_WhenItemNotFound() {
        // Arrange
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> bookingValidator.existsByItemId(itemId));
    }

    @Test
    void existsByItemId_ShouldNotThrow_WhenItemExists() {
        // Arrange
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> bookingValidator.existsByItemId(itemId));
    }

    @Test
    void bookingValidationOfTheItemOwner_ShouldThrowException_WhenUserNotOwner() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;

        User owner = new User();
        owner.setId(999L);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                bookingValidator.bookingValidationOfTheItemOwner(userId, itemId));
    }

    @Test
    void bookingValidationOfTheItemOwner_ShouldNotThrow_WhenUserIsOwner() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;

        User owner = new User();
        owner.setId(userId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));

        // Act & Assert
        assertDoesNotThrow(() -> bookingValidator.bookingValidationOfTheItemOwner(userId, itemId));
    }
}