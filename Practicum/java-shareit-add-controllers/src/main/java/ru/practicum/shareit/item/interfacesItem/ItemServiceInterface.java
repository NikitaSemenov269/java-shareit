package ru.practicum.shareit.item.interfacesItem;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;

public interface ItemServiceInterface {

    Item createItem(Long ownerId, Item newItem);

    Item updateItem(Long ownerId, Item updateItem);

    void deleteItem(Long ownerId, Long itemId);

    ItemDTO getItemDTOById(Long itemId);
}
