package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepository implements ItemRepositoryInterface {

    private final ItemMapper itemMapper;
    private Map<Long, Item> items = new HashMap();


    @Override
    public void addItem(Item item) {
        items.put(item.getItemId(), item);
    }

    @Override
    public Item getItemDTOById(Long itemId) {
        return null;
    }

    @Override
    public Item updateItem(Item updateItem) {
        Item item = items.get(updateItem.getItemId());
        item.setTitle(updateItem.getTitle());
        item.setDescription(updateItem.getDescription());
        item.setAvailable(updateItem.isAvailable());
        item.setRequest(updateItem.getRequest());
        return item;
    }

    @Override
    public void deleteItemById(Long itemId) {

    }

    @Override
    public boolean existsByItemId(Long itemId) {
        return items.containsKey(itemId);
    }
}
