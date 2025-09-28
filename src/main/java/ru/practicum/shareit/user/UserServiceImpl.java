package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataIntegrityException;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidation validation;

    /*
     * Оставил проверку уникальности email только на уровне БД, что бы уменьшить количество
     * обращений к БД.
     */
    @Transactional
    @Override
    public User createUser(User newUser) {
        log.info("Попытка создания нового пользователя.");
        try {
            User user = userRepository.save(newUser);
            log.info("Создан новый пользователь c id: {}", user.getId());
            return user;
        } catch (DataIntegrityViolationException ex) {
            if (isEmailConflict(ex)) {
                log.error("Попытка создания пользователя с существующим email: {}", newUser.getEmail());
                throw new EmailAlreadyExistsException("Пользователь с email " + newUser.getEmail()
                        + " уже существует.");
            } else {
                log.error("Неизвестная ошибка целостности данных: {}", ex.getMessage());
                throw new DataIntegrityException("Ошибка сохранения данных");
            }
        }
    }

    // переделать метод обновления
    @Transactional
    @Override
    public User updateUser(Long id, User updateUser) {
        validation.userValidationId(id);
        log.info("Попытка обновления данных пользователя с ID: {}", id);
        if (userRepository.findByUserId(id) == null) {
            throw new NotFoundException("Пользователь с " + id + " не существует");
        }
        if (userRepository.existsByEmailAndIdNot(updateUser.getEmail(), id)) {
            throw new ValidationException("Email: " + updateUser.getEmail() + " уже занят другим пользователем.");
        }
        User user = userRepository.updateUser(id, updateUser);
        log.info("Данные пользователя с ID: {} успешно обновлены", id);
        return user;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Попытка удаления пользователя по ID.");
        validation.userValidationId(userId);
        userRepository.deleteUserById(userId);
        log.info("Успешное удаления пользователя с ID: {}", userId);
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        log.info("Попытка получения пользователя по ID: {}", userId);
        validation.userValidationId(userId);
        return Optional.ofNullable(userRepository.getUserDTOById(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    private boolean isEmailConflict(DataIntegrityViolationException ex) {
        String message = ex.getMessage().toLowerCase();
        return message.contains("email") &&
                (message.contains("unique") ||
                        message.contains("duplicate") ||
                        message.contains("23505")); // PostgresSQL error code
    }
}

