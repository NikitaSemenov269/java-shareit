package ru.practicum.shareit.user.interfacesUser;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDTO;

public interface UserRepositoryInterface {
    boolean addUser(User newUSer);

//    User getUserDTOById(Long userId);

    UserDTO getUserDTOById(Long userId);

    void deleteUser(Long userId);
}
