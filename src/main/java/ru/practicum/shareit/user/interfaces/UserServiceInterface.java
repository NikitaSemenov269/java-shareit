package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserServiceInterface {

    void addUser(User newUser);

    void updateUser(User updateUser);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    Collection<User> getAllUsers();

}
