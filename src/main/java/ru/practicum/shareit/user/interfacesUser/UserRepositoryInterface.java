package ru.practicum.shareit.user.interfacesUser;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

public interface  UserRepositoryInterface {
    void addUser(User newUSer);

    User updateUser(Long userId, User user);

    User getUserById(Long userId);

    UserDTO getUserDTOById(Long userId);

    void deleteUserById(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByEmailByUserId(String email, Long userId);
}
