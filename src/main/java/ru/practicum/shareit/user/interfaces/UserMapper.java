package ru.practicum.shareit.user.interfaces;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // @Mapping(source = "", target = "") оставляю это тут для освежения памяти в будущем
    UserDto userToUserDto(User user);
}
