/*
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
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

    void bookerValidationById(Long userId) {
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

    void bookingValidationBelongsByIdBooker(Long bookerId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
        );
        if (!bookerId.equals(booking.getBookerId())) {
            throw new ValidationException("ID инициатора брони не cовпадает с ID пользователя.");
        }
    }

    void bookingDateValidation(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new ValidationException("Время начала аренды не может быть null.");
        }
        if (end == null) {
            throw new ValidationException("Время окончания аренды не может быть null.");
        }
        if (end.isBefore(start)) {
            throw new ValidationException("Время окончания аренды не может наступить раньше начала аренды.");
        }
        if (!bookingRepository. (start, end)){
            throw new ValidationException("Бронирование на данный период не возможно т.к. даты уже заняты.");
        }
    }

    void existsByBookerId(Long bookerId) {
        if (!userRepository.existsByUserId(bookerId)) {
            throw new ValidationException("Пользователь с " + bookerId + " не существует");
        }
    }

    void bookingValidationByIdItem(Long itemId) {
        if (!itemRepository.existsByItemId(itemId)) {
            throw new ValidationException("Предмета с " + itemId + " не существует");
        }
    }

    void bookingValidationOfTheItemOwner(Long ownerId, Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        if (!ownerId.equals(item.getOwner())) {
            throw new ValidationException("ID владельца не cовпадает с ID пользователя.");
        }
    }
}*/
