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
    public boolean addItem(Item item) {

    }

    @Override
    public Item getItemDTOById(Long itemId) {
        return null;
    }

    @Override
    public Item updateItem(Long ItemId) {
        return Optional.ofNullable(items.get(ItemId))
                .orElseThrow(() -> new NotFoundException("Предмет с ID: " + ItemId + " не найден"));
    }

    @Override
    public void deleteItemById(Long itemId) {

    }
}
