package ru.practicum.shareit.item.interfacesItem;

import ru.practicum.shareit.item.Item;

public interface ItemRepositoryInterface {

    void addItem(Item item);

    Item getItemDTOById(Long itemId);

    Item updateItem(Item updateItem);

    void deleteItemById(Long itemId);

    boolean existsByItemId(Long itemId);
}
