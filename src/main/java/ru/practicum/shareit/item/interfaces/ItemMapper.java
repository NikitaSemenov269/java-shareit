package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.*;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItem(ItemRequestDto itemRequestDto);

    @Mapping(target = "comments", ignore = true)
    ItemWithBookingAndCommentsDto itemDtoWithBookingAndComments(Item item);

}