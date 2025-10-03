package ru.practicum.shareit.user.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User userDtoToUser(UserDto userDto);
}
