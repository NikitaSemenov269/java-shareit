package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Optional;

@Service
public class UserValidation {

    public void userValidationId(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (userId <= 0) {
            throw new ValidationException("ID пользователя не может быть меньше 0");
        }
    }
}
