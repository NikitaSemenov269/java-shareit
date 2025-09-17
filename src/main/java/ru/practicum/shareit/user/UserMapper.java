package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // @Mapping(source = "", target = "") оставляю это тут для освежения памяти в будущем
    UserDto userToUserDto(User user);
}
