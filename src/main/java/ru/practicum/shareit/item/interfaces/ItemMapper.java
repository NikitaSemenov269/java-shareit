package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.*;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "request", ignore = true)
    Item toItem(ItemRequestDto itemRequestDto);

    @Mapping(target = "comments", ignore = true)
    ItemWithBookingAndCommentsDto itemDtoWithBookingAndComments(Item item);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDtoForRequester toDtoForRequest(Item item);
}