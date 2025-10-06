package ru.practicum.shareit.request.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "requesterId", source = "requester.id")
    RequestDto toDto(Request request);
}