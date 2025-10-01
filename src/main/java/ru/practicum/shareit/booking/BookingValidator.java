package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
class BookingValidator {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    void userValidationById(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (userId <= 0) {
            throw new ValidationException("ID пользователя не может быть меньше 0");
        }
    }

    void bookingValidationById(Long bookingId) {
        if (bookingId == null) {
            throw new ValidationException("ID заявки не может быть null");
        }
        if (bookingId <= 0) {
            throw new ValidationException("ID заявки не может быть меньше 0");
        }
    }

    void bookingDateValidation(Long itemId, LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new ValidationException("Время начала аренды не может быть null.");
        }
        if (end == null) {
            throw new ValidationException("Время окончания аренды не может быть null.");
        }
        if (end.isBefore(start)) {
            throw new ValidationException("Время окончания аренды не может наступить раньше начала аренды.");
        }
        if (end.equals(start)) {
            throw new ValidationException("Время начала и окончания аренды не могут совпадать.");
        }
        if (bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(itemId, end, start)) {
            throw new ValidationException("Бронирование на данный период невозможно поскольку даты уже заняты.");
        }
    }

    void existsByUserId(Long bookerId) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("Пользователь с " + bookerId + " не найден.");
        }
    }

    void existsByItemId(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Предмета с " + itemId + " не найден.");
        }
    }

    void bookingValidationOfTheItemOwner(Long ownerId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмета с " + itemId + " не найден."));

        if (!ownerId.equals(item.getOwner().getId())) {
            throw new ValidationException("ID владельца не cовпадает с ID пользователя.");
        }
    }
}

