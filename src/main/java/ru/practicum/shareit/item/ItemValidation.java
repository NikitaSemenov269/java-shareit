package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor

public class ItemValidation {

    public void itemValidationId(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (itemId <= 0) {
            throw new ValidationException("ID предмета не может быть меньше 0");
        }
    }

    public void itemValidationOwnerId(Long ownerId) {
        if (ownerId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (ownerId <= 0) {
            throw new ValidationException("ID предмета не может быть меньше 0");
        }
    }

    public void itemDateValidation(LocalDate startDateBooking, LocalDate endDateBooking) {
        if (startDateBooking == null) {
            throw new ValidationException("Время начала аренды не может быть null.");
        }
        if (endDateBooking == null) {
            throw new ValidationException("Время окончания аренды не может быть null.");
        }
        if (startDateBooking.isBefore(LocalDate.now())) {
            throw new ValidationException("Время начала аренды не может быть в прошлом.");
        }
        if (endDateBooking.isBefore(startDateBooking)) {
            throw new ValidationException("Время окончания аренды не может наступить раньше начала аренды.");
        }
    }
}

