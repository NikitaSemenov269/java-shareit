package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserRepositoryInterface;

import java.util.*;
import java.util.stream.Collectors;

@Repository // планируется переход на работу с БД
public class UserRepository implements UserRepositoryInterface {

    private Set<User> users = new HashSet<>();

    @Override
    public boolean addUser(User newUser) {
        return users.add(newUser);
    }

    @Override
    public boolean deleteUser(Long userId) {
        return users.remove(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
