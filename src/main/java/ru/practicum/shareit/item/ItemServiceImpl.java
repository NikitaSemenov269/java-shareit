package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemValidation itemValidation;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, Item newItem) {
        if (newItem.getAvailable() == null) {
            throw new ValidationException("Поле available обязательно");
        }

        log.info("Попытка создания нового предмета.");

        itemValidation.itemValidationByOwnerId(ownerId);

        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID: " + ownerId + " не найден"));

        newItem.setOwner(owner);
        itemRepository.save(newItem);
        log.info("Создан новый предмет с ID: {}", newItem.getId());
        return itemMapper.itemToItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long ownerId, Item updateItem) {

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        log.info("Попытка обновления данных предмета с ID: {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id: " + itemId + " не найден.")
        );

        if (updateItem.getName() != null && !updateItem.getName().equals(item.getName())) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().equals(item.getDescription())) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null && !updateItem.getAvailable().equals(item.getAvailable())) {
            // подумать / почитать, что в новой версии приходит на вход контроллера и с чем работаем
        }

        log.info("Данные предмета с ID: {} успешно обновлены", itemId);
        // Изменения сохранятся при коммите транзакции
        return itemMapper.itemToItemDto(item);
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        log.info("Попытка удаления предмета ID: {} пользователем с ID: {}", itemId, ownerId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        itemRepository.deleteById(itemId);
        log.info("Успешное удаление предмета ID: {} пользователем с ID: {}", itemId, ownerId);
    }

    @Override
    public ItemWithBookingDto getItemDTOById(Long itemId) {
        log.info("Попытка получения предмета по ID: {}", itemId);

        itemValidation.itemValidationById(itemId);

        return itemMapper.itemWithBookingDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с ID: " + itemId + " не найден")));
    }

    @Override
    public Collection<ItemDto> searchItemDtoByText(String text) {
        log.info("Попытка поиска доступных предметов по ключевым словам: {}", text);

        if (text == null || text.trim().isEmpty()) {
            log.info("Поиска доступных предметов не дал результата.");
            return Collections.emptyList();
        }
        return itemRepository.findAllByText(text.trim());
    }

    @Override
    public Collection<ItemWithBookingDto> searchAllItemOfOwnerById(Long ownerId) {
        log.info("Попытка поиска всех предметов пользователя с ID: {}", ownerId);

        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);

        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public void updateItemAvailable(Long ownerId, Long itemId, BookingStatus bookingStatus) {
        log.info("Попытка обновления статуса брони предмета с ID: {}", itemId);

        itemValidation.itemValidationById(itemId);
        itemValidation.itemValidationByOwnerId(ownerId);
        itemValidation.existsByUserId(ownerId);
        itemValidation.itemValidationBelongsByIdOwner(ownerId, itemId);

        if (bookingStatus == null) {
            throw new ValidationException("Статус бронирования не может быть null");
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id: " + itemId + " не найден."));

        item.setAvailable(bookingStatus.isStatus());

        itemRepository.save(item);
        log.info("Успешное обновление статуса брони предмета с ID: {}", itemId);
    }
}