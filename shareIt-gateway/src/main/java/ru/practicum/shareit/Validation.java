package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Validation {

    void userIdValidation(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (userId <= 0) {
            throw new ValidationException("ID пользователя не может быть меньше 0");
        }
    }

    void requestIdValidation(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID запроса не может быть null");
        }
        if (userId <= 0) {
            throw new ValidationException("ID запроса не может быть меньше 0");
        }
    }

    void itemIdValidation(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (itemId <= 0) {
            throw new ValidationException("ID предмета не может быть меньше 0");
        }
    }

    void bookingIdValidation(Long bookingId) {
        if (bookingId == null) {
            throw new ValidationException("ID заявки не может быть null");
        }
        if (bookingId <= 0) {
            throw new ValidationException("ID заявки не может быть меньше 0");
        }
    }

    void userEmailValidation(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустой строкой, или null");
        }
    }

    void userNameValidation(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Имя не может быть пустой строкой, или null");
        }
    }

    void dateValidation(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new ru.practicum.exception.ValidationException("Время начала аренды не может быть null.");
        }
        if (end == null) {
            throw new ru.practicum.exception.ValidationException("Время окончания аренды не может быть null.");
        }
        if (end.isBefore(start)) {
            throw new ru.practicum.exception.ValidationException("Время окончания аренды не может наступить раньше начала аренды.");
        }
        if (end.equals(start)) {
            throw new ru.practicum.exception.ValidationException("Время начала и окончания аренды не могут совпадать.");
        }
    }
}

