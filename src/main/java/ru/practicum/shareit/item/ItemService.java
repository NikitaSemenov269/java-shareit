package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;
import ru.practicum.shareit.item.interfacesItem.ItemServiceInterface;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService implements ItemServiceInterface {

    private final ItemValidation itemValidation;
    private final ItemRepositoryInterface itemRepositoryInt;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Item createItem(Item newItem) {
        newItem.setItemId(counter.getAndIncrement());
        Long id = newItem.getItemId();
        log.info("Попытка создания нового предмета.");
        itemValidation.itemValidationId(id);
        if (!itemRepositoryInt.addItem(newItem)) {
            throw new ValidationException("Предмет с " + id + " уже существует");
        }
        log.info("Создан новый предмет с ID: {}", id);
        return newItem;
    }

    @Override
    public Item updateItem(Item updateItem) {
        itemValidation.itemValidationId(updateItem.getItemId());
        Long updateItemId = updateItem.getItemId();
        log.info("Попытка обновления данных предмета с ID: {}", updateItemId);
        itemValidation.itemDateValidation(updateItem.getStartDateBooking(),updateItem.getEndDateBooking());
        itemRepositoryInt.deleteItemById(updateItemId);
        itemRepositoryInt.addItem(updateItem);
        log.info("Данные предмета с ID: {} успешно обновлены", updateItemId);
        return updateItem;
    }
}



