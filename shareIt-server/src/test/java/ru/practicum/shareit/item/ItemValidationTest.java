package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
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
    void itemValidationBelongsByIdOwner_WithNonExistingItem_ShouldThrowNotFoundException() {
        Long ownerId = 1L;
        Long itemId = 999L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId));
    }
}