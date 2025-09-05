package ru.practicum.shareit.user.interfacesUser;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

public interface UserServiceInterface {

    void createUser(User newUser);

    void updateUser(User updateUser);

    void deleteUser(Long userId);

   //User getUserById(Long userId);

    UserDTO getUserDTOById(Long userId);
}
