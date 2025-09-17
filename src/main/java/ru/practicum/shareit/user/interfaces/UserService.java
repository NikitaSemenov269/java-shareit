package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public interface UserService {

    User createUser(User newUser);

    User updateUser(Long userId, User updateUser);

    void deleteUser(Long userId);

    UserDto getUserDtoById(Long userId);
}