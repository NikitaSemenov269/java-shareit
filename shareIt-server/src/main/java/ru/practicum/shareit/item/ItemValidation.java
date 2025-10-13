package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.interfaces.UserRepository;

@Component
@RequiredArgsConstructor
public class ItemValidation {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public void itemValidationBelongsByIdOwner(Long ownerId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id: " + itemId + " не найден."));

        if (!ownerId.equals(item.getOwner().getId())) {
            throw new ValidationException("Операция отклонена." +
                    " ID владельца не совпадает с ID пользователя.");
        }
    }

    public void existsByUserId(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
    }
}

