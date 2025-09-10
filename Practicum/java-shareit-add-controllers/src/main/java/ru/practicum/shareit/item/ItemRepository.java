package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepository implements ItemRepositoryInterface {

    private final ItemMapper itemMapper;
    private Map<Long, Item> items = new HashMap<>();

    @Override
    public void addItem(Item item) {
        items.put(item.getItemId(), item);
    }

    @Override
    public ItemDTO getItemDTOById(Long itemId) {
        return itemMapper.toItemDTO(items.get(itemId));
    }

    @Override
    public Item updateItem(Item updateItem) {
        Item item = items.get(updateItem.getItemId());
        item.setTitle(updateItem.getTitle());
        item.setDescription(updateItem.getDescription());
        item.setAvailable(updateItem.isAvailable());
        return item;
    }

    @Override
    public void deleteItemById(Long itemId) {
        items.remove(itemId);
    }

    @Override
    public boolean existsByItemId(Long itemId) {
        return items.containsKey(itemId);
    }
    //служебный метод
    protected Item getItemById(Long itemId) {
        return items.get(itemId);
    }
}
