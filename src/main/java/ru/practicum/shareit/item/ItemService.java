package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;
import ru.practicum.shareit.item.interfacesItem.ItemServiceInterface;
import ru.practicum.shareit.user.interfacesUser.UserRepositoryInterface;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService implements ItemServiceInterface {

    private final ItemValidation itemValidation;
    private final ItemRepositoryInterface itemRepositoryInterface;
    private final UserRepositoryInterface userRepositoryInterface;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Item createItem(Long ownerId, Item newItem) {
        newItem.setItemId(counter.getAndIncrement());
        Long id = newItem.getItemId();
        log.info("Попытка создания нового предмета.");
        itemValidation.itemValidationById(id);
        itemValidation.itemValidationByOwnerId(ownerId);
        if (!userRepositoryInterface.existsByUserId(ownerId)) {
            throw new ValidationException("Владелец с " + ownerId + " не существует");
        }
        newItem.setOwnerId(ownerId);
        itemRepositoryInterface.addItem(newItem);
        log.info("Создан новый предмет с ID: {}", id);
        return newItem;
    }

    @Override
    public Item updateItem(Long ownerId, Item updateItem) {
        itemValidation.itemValidationById(updateItem.getItemId());
        itemValidation.itemValidationByOwnerId(ownerId);
        if (!userRepositoryInterface.existsByUserId(ownerId)) {
            throw new ValidationException("Владелец с " + ownerId + " не существует");
        }
        itemValidation.itemValidationBelongsByIdOwner(ownerId, updateItem);
        Long updateItemId = updateItem.getItemId();
        log.info("Попытка обновления данных предмета с ID: {}", updateItemId);
        Item item = itemRepositoryInterface.updateItem(updateItem);
        log.info("Данные предмета с ID: {} успешно обновлены", updateItemId);
        return item;
    }

    @Override
    public void deleteItem(Long ownerId, Item item) {
        log.info("Попытка удаления предмета ID: {}", item.getItemId());
        itemValidation.itemValidationById(item.getItemId());
        itemValidation.itemValidationByOwnerId(ownerId);
        if (!userRepositoryInterface.existsByUserId(ownerId)) {
            throw new ValidationException("Владелец с " + ownerId + " не существует");
        }
        itemValidation.itemValidationBelongsByIdOwner(ownerId, item);

        log.info("Успешное удаление предмета ID: {}", item.getItemId());
    }
}



