package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemValidationTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemValidation itemValidation;

    @Test
    void existsByUserId_WithExistingUser_ShouldNotThrowException() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> itemValidation.existsByUserId(userId));
    }

    @Test
    void existsByUserId_WithNonExistingUser_ShouldThrowNotFoundException() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemValidation.existsByUserId(userId));
    }

    @Test
    void itemValidationBelongsByIdOwner_WithValidOwner_ShouldNotThrowException() {
        Long ownerId = 1L;
        Long itemId = 1L;

        User owner = new User();
        owner.setId(ownerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId));
    }

    @Test
    void itemValidationBelongsByIdOwner_WithInvalidOwner_ShouldThrowValidationException() {
        Long ownerId = 1L;
        Long itemId = 1L;
        Long differentOwnerId = 2L;

        User differentOwner = new User();
        differentOwner.setId(differentOwnerId);

        Item item = new Item();
        item.setId(itemId);
        item.setOwner(differentOwner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId));
    }
}