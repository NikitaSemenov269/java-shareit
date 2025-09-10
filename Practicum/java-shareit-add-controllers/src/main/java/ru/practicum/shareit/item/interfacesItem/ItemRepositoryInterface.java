package ru.practicum.shareit.item.interfacesItem;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDTO;

public interface ItemRepositoryInterface {

    void addItem(Item item);

    ItemDTO getItemDTOById(Long itemId);

    Item updateItem(Item updateItem);

    void deleteItemById(Long itemId);

    boolean existsByItemId(Long itemId);
}
