package ru.practicum.shareit.item.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.ItemWithCommentsDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto itemToItemDto(Item item);

    @Mapping(target = "comments", ignore = true)
    ItemWithCommentsDto itemDtoWithComments(Item item);

    @Mapping(target = "comments", ignore = true)
    ItemWithBookingAndCommentsDto itemDtoWithBookingAndComments(Item item);

}