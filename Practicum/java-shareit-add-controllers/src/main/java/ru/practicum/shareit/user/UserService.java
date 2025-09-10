package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfacesUser.UserRepositoryInterface;
import ru.practicum.shareit.user.interfacesUser.UserServiceInterface;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepositoryInterface userRepositoryInterface;
    private final UserValidation validation;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public User createUser(User newUser) {
        newUser.setUserId(counter.getAndIncrement());
        Long userId = newUser.getUserId();
        log.info("Попытка создания нового пользователя.");
        validation.userValidationId(userId);
        if (userRepositoryInterface.existsByUserId(userId)) {
            throw new ValidationException("Пользователь с " + userId + " уже существует");
        }
        if (userRepositoryInterface.existsByEmailByUserId(newUser.getEmail(), userId)) {
            throw new ValidationException("Пользователь с " + newUser.getEmail() + " уже существует");
        }
        userRepositoryInterface.addUser(newUser);

        log.info("Создан новый пользователь с ID: {}", userId);
        return newUser;
    }

    @Override
    public User updateUser(Long userId, User updateUser) {
        validation.userValidationId(userId);
        log.info("Попытка обновления данных пользователя с ID: {}", userId);
        if (!userRepositoryInterface.existsByUserId(userId)) {
            throw new ValidationException("Пользователь с " + userId + " не существует");
        }
        if (userRepositoryInterface.existsByEmailByUserId(updateUser.getEmail(), userId)) {
            throw new ValidationException("Пользователь с " + updateUser.getEmail() + " уже существует");
        }
        User user = userRepositoryInterface.updateUser(userId, updateUser);
        log.info("Данные пользователя с ID: {} успешно обновлены", userId);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Попытка удаления пользователя по ID.");
        validation.userValidationId(userId);
        userRepositoryInterface.deleteUserById(userId);
        log.info("Успешное удаления пользователя с ID: {}", userId);
    }

    @Override
    public UserDTO getUserDTOById(Long userId) {
        log.info("Попытка получения пользователя по ID: {}", userId);
        validation.userValidationId(userId);
        return Optional.ofNullable(userRepositoryInterface.getUserDTOById(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }
}
