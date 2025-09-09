package ru.practicum.shareit.item.interfacesItem;

import ru.practicum.shareit.item.Item;

public interface ItemRepositoryInterface {

    boolean addItem(Item item);

    Item getItemDTOById(Long itemId);

    Item updateItem(Long ItemId);

    void deleteItemById(Long itemId);
}
