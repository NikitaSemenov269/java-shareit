package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepositoryInterface {
    boolean addUser(User newUSer);

    Optional<User> getUserById(Long userId);

    Collection<User> getAllUsers();

    boolean deleteUser(Long userId);
}
