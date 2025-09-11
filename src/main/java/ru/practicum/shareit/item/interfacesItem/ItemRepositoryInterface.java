package ru.practicum.shareit.item.interfacesItem;

import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;

import java.util.Collection;

public interface ItemRepositoryInterface {

    void addItem(Item item);

    ItemDTO getItemDTOById(Long itemId);

    Item updateItem(Item updateItem);

    void deleteItemById(Long itemId);

    boolean existsByItemId(Long itemId);

    Collection<ItemDTO> searchItemDtoByText(String text);

    Collection<ItemDTO> searchAllItemOfOwnerById(Long ownerId);

    Item updateItemAvailable(Long itemId, BookingStatus bookingStatus);
}
