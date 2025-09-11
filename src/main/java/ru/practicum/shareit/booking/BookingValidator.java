package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;
import ru.practicum.shareit.user.interfacesUser.UserRepositoryInterface;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
class BookingValidator {
    private final BookingRepository bookingRepository;
    private final UserRepositoryInterface userRepositoryInterface;
    private final ItemRepositoryInterface itemRepositoryInterface;

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
        Booking booking = bookingRepository.getBookingById(bookingId);
        if (!bookerId.equals(booking.getBookerId())) {
            throw new ValidationException("ID инициатора брони не cовпадает с ID пользователя.");
        }
    }

    void bookingDateValidation(LocalDate startRent, LocalDate endRent) {
        if (startRent == null) {
            throw new ValidationException("Время начала аренды не может быть null.");
        }
        if (endRent == null) {
            throw new ValidationException("Время окончания аренды не может быть null.");
        }
        if (endRent.isBefore(startRent)) {
            throw new ValidationException("Время окончания аренды не может наступить раньше начала аренды.");
        }
        if (!bookingRepository.checkingBookingDates(startRent, endRent)) {
            throw new ValidationException("Бронирование на данный период не возможно т.к. даты уже заняты.");
        }
    }

    void existsByBookingId(Long bookerId) {
        if (!userRepositoryInterface.existsByUserId(bookerId)) {
            throw new ValidationException("Пользователь с " + bookerId + " не существует");
        }
    }

    void bookingValidationByIdItem(Long bookingId) {
        if (!itemRepositoryInterface.existsByItemId(bookingId)) {
            throw new ValidationException("Предмета с " + bookingId + " не существует");
        }
    }
}
