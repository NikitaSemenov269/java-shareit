package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfaces.UserRepositoryInterface;
import ru.practicum.shareit.user.interfaces.UserServiceInterface;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepositoryInterface userRepository;
    private final UserValidation validation;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public void addUser(User newUser) {
        newUser.setUserId(counter.getAndIncrement());
        Long id = newUser.getUserId();
        log.info("Попытка создания нового пользователя.");
        validation.userValidationId(id);
        if (!userRepository.addUser(newUser)) {
            throw new ValidationException("Пользователь с " + id + " уже существует");
        }
        log.info("Создан новый пользователь с ID: {}", id);
    }

    @Override
    public void updateUser(User updateUser) {
        validation.userValidationId(updateUser.getUserId());
        Long updateUserId = updateUser.getUserId();
        log.info("Попытка обновления данных пользователя с ID: {}", updateUserId);
        if (!userRepository.deleteUser(updateUserId)) {
            throw new ValidationException("Не удалось удалить пользователя с ID: " + updateUserId +
                    " во время обновления данных.");
        }
        if (!userRepository.addUser(updateUser)) {
            throw new ValidationException("Пользователь с " + updateUserId + " уже существует. Обновление невозможно");
        }
        log.info("Пользователь с ID: {} успешно обновлен", updateUserId);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Попытка удаления пользователя по ID.");
        validation.userValidationId(userId);
        if (!userRepository.deleteUser(userId)) {
            throw new ValidationException("Не удалось удалить пользователя с ID:" + userId);
        }
        log.info("Успешное удаления пользователя с ID: {}", userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
