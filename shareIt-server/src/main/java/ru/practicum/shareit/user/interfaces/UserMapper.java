package ru.practicum.shareit.user.interfaces;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserRequestDto userRequestDto);
}
