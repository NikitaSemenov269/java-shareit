package ru.practicum.shareit.user.interfaces;

import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;

public interface UserService {

    UserDto createUser(UserRequestDto userRequestDto);

    UserDto updateUser(Long userId, UserRequestDto userRequestDto);

    void deleteUser(Long userId);

    UserDto getUserDtoById(Long userId);
}