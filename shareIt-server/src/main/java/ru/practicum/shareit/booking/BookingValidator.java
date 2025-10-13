package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
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

    void bookingDateValidation(Long itemId, LocalDateTime start, LocalDateTime end) {
        if (bookingRepository.existsByItemIdAndStartLessThanEqualAndEndGreaterThanEqual(itemId, start, end)) {
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

