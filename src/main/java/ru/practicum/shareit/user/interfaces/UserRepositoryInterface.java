package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserRepositoryInterface {
    boolean addUser(User newUSer);

    User getUserById(Long userId);

    Collection<User> getAllUsers();

    boolean deleteUser(Long userId);
}
