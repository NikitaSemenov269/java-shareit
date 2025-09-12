package ru.practicum.shareit.item.interfacesItem;

import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;

import java.util.Collection;

public interface ItemServiceInterface {

    Item createItem(Long ownerId, Item newItem);

    Item updateItem(Long itemId, Long ownerId, Item updateItem);

    void deleteItem(Long ownerId, Long itemId);

    ItemDTO getItemDTOById(Long itemId);

    Collection<ItemDTO> searchItemDtoByText(String text);

    Collection<ItemDTO> searchAllItemOfOwnerById(Long ownerId);

    Item updateItemAvailable(Long ownerId, Long itemId, BookingStatus bookingStatus);
}
