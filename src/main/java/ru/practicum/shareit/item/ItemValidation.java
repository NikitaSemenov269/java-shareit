package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor

public class ItemValidation {

    public void itemValidationById(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (itemId <= 0) {
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

    public void itemValidationBelongsByIdOwner(Long ownerId, Item item) {
        if (!ownerId.equals(item.getOwnerId())) {
            throw new ValidationException("ID владельца не cовпадает с ID пользователя.");
        }
    }

    //перенести в другой валидатор
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

