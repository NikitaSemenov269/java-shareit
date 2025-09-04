package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserServiceInterface {

    void addUser(User newUser);

    User updateUser(User updateUser);

    void deleteUser(Long userId);

    Optional<User> getUserById(Long userId);

    Collection<User> getAllUsers();

}
