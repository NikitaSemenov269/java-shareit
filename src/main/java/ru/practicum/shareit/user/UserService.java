package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfaces.UserRepositoryInterface;
import ru.practicum.shareit.user.interfaces.UserServiceInterface;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        if (newUser.getUserId() == null) {
            newUser.setUserId(counter.getAndIncrement());
        }
        log.info("Попытка создания нового пользователя.");
        validation.userValidation(newUser);
        if (!userRepository.addUser(newUser)) {
            throw new ValidationException("Пользователь с " + newUser.getUserId() + " уже существует");
        }
        log.info("Создан новый пользователь с ID: {}", newUser.getUserId());
    }

    @Override
    public User updateUser(User updateUser) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return null;
    }

    @Override
    public Collection<User> getAllUsers() {
        return List.of();
    }
}
