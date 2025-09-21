package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemValidation itemValidation;
    private final ItemRepository itemRepository;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Item createItem(Long owner, Item newItem) {
        if (newItem.getAvailable() == null) {
            throw new ValidationException("Поле available обязательно");
        }

        newItem.setId(counter.getAndIncrement());
        Long id = newItem.getId();
        log.info("Попытка создания нового предмета.");

        itemValidation.itemValidationById(id);
        itemValidation.itemValidationByOwnerId(owner);
        itemValidation.existsByUserId(owner);

        newItem.setOwner(owner);
        itemRepository.addItem(newItem);
        log.info("Создан новый предмет с ID: {}", id);
        return newItem;
    }

    @Override
    public Item updateItem(Long itemId, Long ownerId, Item updateItem) {

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        log.info("Попытка обновления данных предмета с ID: {}", itemId);
        updateItem.setId(itemId);
        updateItem.setOwner(ownerId);
        Item item = itemRepository.updateItem(updateItem);
        log.info("Данные предмета с ID: {} успешно обновлены", itemId);
        return item;
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        log.info("Попытка удаления предмета ID: {}", itemId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        itemRepository.deleteItemById(itemId);
        log.info("Успешное удаление предмета ID: {}", itemId);
    }

    @Override
    public ItemDto getItemDTOById(Long itemId) {
        log.info("Попытка получения предмета по ID: {}", itemId);

        itemValidation.itemValidationById(itemId);

        return Optional.ofNullable(itemRepository.getItemDTOById(itemId))
                .orElseThrow(() -> new NotFoundException("Предмет с ID: " + itemId + " не найден"));
    }

    @Override
    public Collection<ItemDto> searchItemDtoByText(String text) {
        log.info("Попытка поиска доступных предметов по ключевым словам: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemDtoByText(text);
    }

    @Override
    public Collection<ItemDto> searchAllItemOfOwnerById(Long ownerId) {
        log.info("Попытка поиска всех предметов пользователя с ID: {}", ownerId);

        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);

        return itemRepository.searchAllItemOfOwnerById(ownerId);
    }

    @Override
    public Item updateItemAvailable(Long ownerId, Long itemId, BookingStatus bookingStatus) {
        log.info("Попытка обновления статуса брони предмета с ID: {}", itemId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        if (bookingStatus == null) {
            throw new ValidationException("Статус бронирования не может быть null");
        }

        return itemRepository.updateItemAvailable(itemId, bookingStatus);
    }
}