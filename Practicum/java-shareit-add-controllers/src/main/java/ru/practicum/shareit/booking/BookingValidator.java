package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BookingValidator {
    private final BookingRepository bookingRepository;

    public void bookerValidationById(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (userId <= 0) {
            throw new ValidationException("ID пользователя не может быть меньше 0");
        }
    }

    public void bookingValidationById(Long bookingId) {
        if (bookingId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (bookingId <= 0) {
            throw new ValidationException("ID предмета не может быть меньше 0");
        }
    }

    public void itemValidationByOwnerId(Long ownerId) {
        if (ownerId == null) {
            throw new ValidationException("ID владельца не может быть null");
        }
        if (ownerId <= 0) {
            throw new ValidationException("ID владельца не может быть меньше 0");
        }
    }

    public void bookingValidationBelongsByIdOwner(Long bookerId, Long itemId) {
        Booking booking = bookingRepository.getBookingById(itemId);
        if (!bookerId.equals(booking.getBookerId())) {
            throw new ValidationException("ID владельца не cовпадает с ID пользователя.");
        }
    }

    public void bookingDateValidation(LocalDate startRent, LocalDate endRent) {
        if (startRent == null) {
            throw new ValidationException("Время начала аренды не может быть null.");
        }
        if (endRent == null) {
            throw new ValidationException("Время окончания аренды не может быть null.");
        }
        if (endRent.isBefore(startRent)) {
            throw new ValidationException("Время окончания аренды не может наступить раньше начала аренды.");
        }
    }
}
