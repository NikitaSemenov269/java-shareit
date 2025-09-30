package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemWithBookingDto;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long ownerId, Item newItem);

    ItemDto updateItem(Long itemId, Long ownerId, Item updateItem);

    void deleteItem(Long ownerId, Long itemId);

    ItemWithBookingDto getItemDTOById(Long itemId);

    Collection<ItemDto> searchItemDtoByText(String text);

    Collection<ItemWithBookingDto> searchAllItemOfOwnerById(Long ownerId);

    void updateItemAvailable(Long ownerId, Long itemId, BookingStatus bookingStatus);
}
