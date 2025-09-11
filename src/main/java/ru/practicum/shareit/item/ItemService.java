package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;
import ru.practicum.shareit.item.interfacesItem.ItemServiceInterface;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService implements ItemServiceInterface {

    private final ItemValidation itemValidation;
    private final ItemRepositoryInterface itemRepositoryInterface;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Item createItem(Long ownerId, Item newItem) {
        newItem.setItemId(counter.getAndIncrement());
        Long id = newItem.getItemId();
        log.info("Попытка создания нового предмета.");

        itemValidation.itemValidationById(id);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);

        newItem.setOwnerId(ownerId);
        itemRepositoryInterface.addItem(newItem);
        log.info("Создан новый предмет с ID: {}", id);
        return newItem;
    }

    @Override
    public Item updateItem(Long ownerId, Item updateItem) {

        itemValidation.itemValidationById(updateItem.getItemId());
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, updateItem.getItemId());

        Long updateItemId = updateItem.getItemId();
        log.info("Попытка обновления данных предмета с ID: {}", updateItemId);
        Item item = itemRepositoryInterface.updateItem(updateItem);
        log.info("Данные предмета с ID: {} успешно обновлены", updateItemId);
        return item;
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        log.info("Попытка удаления предмета ID: {}", itemId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        itemRepositoryInterface.deleteItemById(itemId);
        log.info("Успешное удаление предмета ID: {}", itemId);
    }

    @Override
    public ItemDTO getItemDTOById(Long itemId) {
        log.info("Попытка получения предмета по ID: {}", itemId);

        itemValidation.itemValidationById(itemId);

        return Optional.ofNullable(itemRepositoryInterface.getItemDTOById(itemId))
                .orElseThrow(() -> new NotFoundException("Предмет с ID: " + itemId + " не найден"));
    }

    @Override
    public Collection<ItemDTO> searchItemDtoByText(String text) {
        log.info("Попытка поиска доступных предметов по ключевым словам: {}", text);
        return itemRepositoryInterface.searchItemDtoByText(text);
    }

    @Override
    public Collection<ItemDTO> searchAllItemOfOwnerById(Long ownerId) {
        log.info("Попытка поиска всех предметов пользователя с ID: {}", ownerId);

        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);

        return itemRepositoryInterface.searchAllItemOfOwnerById(ownerId);
    }

    // Требует доработки !!!
    @Override
    public Item updateItemAvailable(Long ownerId, Long itemId, BookingStatus bookingStatus) {
        log.info("Попытка обновления статуса брони предмета с ID: {}", itemId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        return itemRepositoryInterface.updateItemAvailable(itemId, bookingStatus);
    }
}



