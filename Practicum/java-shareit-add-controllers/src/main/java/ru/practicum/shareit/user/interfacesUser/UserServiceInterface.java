package ru.practicum.shareit.user.interfacesUser;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

public interface UserServiceInterface {

    User createUser(User newUser);

    User updateUser(Long userId, User updateUser);

    void deleteUser(Long userId);

    UserDTO getUserDTOById(Long userId);
}
