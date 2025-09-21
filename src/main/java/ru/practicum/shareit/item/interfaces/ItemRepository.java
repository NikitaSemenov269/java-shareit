package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

import java.util.Collection;

public interface ItemRepository {

    void addItem(Item item);

    ItemDto getItemDTOById(Long itemId);

    Item updateItem(Item updateItem);

    void deleteItemById(Long itemId);

    boolean existsByItemId(Long itemId);

    Collection<ItemDto> searchItemDtoByText(String text);

    Collection<ItemDto> searchAllItemOfOwnerById(Long ownerId);

    Item updateItemAvailable(Long itemId, BookingStatus bookingStatus);

    Item getItemById(Long itemId);
}
