/*
package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.interfaces.UserRepository;

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
    void existsByItemId_WithExistingItem_ShouldNotThrowException() {
        Long itemId = 1L;
        when(itemRepository.existsById(itemId)).thenReturn(true);

        assertDoesNotThrow(() -> bookingValidator.existsByItemId(itemId));
    }

    @Test
    void existsByItemId_WithNonExistingItem_ShouldThrowNotFoundException() {
        Long itemId = 999L;
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingValidator.existsByItemId(itemId));
    }

    @Test
    void bookingValidationOfTheItemOwner_WithValidOwner_ShouldNotThrowException() {
        // This method needs to be implemented in BookingValidator
        // Currently it's missing from the provided code
    }
}*/
