package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;

public interface ItemService {

    Item createItem(Long ownerId, Item newItem);

    Item updateItem(Long itemId, Long ownerId, Item updateItem);

    void deleteItem(Long ownerId, Long itemId);

    ItemDto getItemDTOById(Long itemId);

    Collection<ItemDto> searchItemDtoByText(String text);

    Collection<ItemDto> searchAllItemOfOwnerById(Long ownerId);

    Item updateItemAvailable(Long ownerId, Long itemId, BookingStatus bookingStatus);
}
