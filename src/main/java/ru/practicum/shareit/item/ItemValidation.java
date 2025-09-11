package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ItemValidation {
    private final ItemRepository itemRepository;

    public void itemValidationById(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (itemId <= 0) {
            throw new ValidationException("ID предмета не может быть меньше 0");
        }
    }

    public void itemValidationByOwnerId(Long ownerId) {
        if (ownerId == null) {
            throw new ValidationException("ID владельца не может быть null");
        }
        if (ownerId <= 0) {
            throw new ValidationException("ID владельца не может быть меньше 0");
        }
    }

    public void itemValidationBelongsByIdOwner(Long ownerId, Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        if (!ownerId.equals(item.getOwnerId())) {
            throw new ValidationException("ID владельца не cовпадает с ID пользователя.");
        }
    }
}

