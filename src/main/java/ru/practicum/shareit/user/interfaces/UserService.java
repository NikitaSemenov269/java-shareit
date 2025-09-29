package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public interface UserService {

    UserDto createUser(User newUser);

    UserDto updateUser(Long userId, UserDto updateUser);

    void deleteUser(Long userId);

    UserDto getUserDtoById(Long userId);
}