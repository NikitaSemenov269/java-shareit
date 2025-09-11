package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.interfacesItem.ItemRepositoryInterface;

import java.util.*;
import java.util.stream.Collectors;

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
        if (updateItem.getTitle() != null) {
            item.setTitle(updateItem.getTitle());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        return item;
    }

    // Требует доработки !!!
    @Override
    public Item updateItemAvailable(Long itemId, BookingStatus bookingStatus) {
        Item item = items.get(itemId);
        if ( !bookingStatus.equals(item.getAvailable()) ) {
            item.setAvailable(bookingStatus);
        }
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

    @Override
    public Collection<ItemDTO> searchItemDtoByText(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item ->
                        (item.getTitle().toLowerCase().contains(searchText)) ||
                                (item.getDescription() != null && item.getDescription()
                                        .toLowerCase().contains(searchText))
                )
                .filter(item -> item.getAvailable().isStatus())
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDTO> searchAllItemOfOwnerById(Long ownerId) {
        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    //служебный метод
    protected Item getItemById(Long itemId) {
        return items.get(itemId);
    }
}