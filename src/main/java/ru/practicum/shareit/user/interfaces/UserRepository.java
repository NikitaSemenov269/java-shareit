package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public interface UserRepository {
    void addUser(User newUSer);

    User updateUser(Long userId, User user);

    User getUserById(Long userId);

    UserDto getUserDTOById(Long userId);

    void deleteUserById(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByEmailByUserId(String email, Long userId);
}
