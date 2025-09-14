package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
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
        items.put(item.getId(), item);
    }

    @Override
    public ItemDTO getItemDTOById(Long itemId) {
        return itemMapper.toItemDTO(items.get(itemId));
    }

    @Override
    public Item updateItem(Item updateItem) {
        Item item = items.get(updateItem.getId());

        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        if (updateItem.getRequest() != null) {
            item.setRequest(updateItem.getRequest());
        }

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItemAvailable(Long itemId, BookingStatus bookingStatus) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с ID: " + itemId + " не найден");
        }

        Boolean currentAvailable = item.getAvailable();
        boolean newStatus = bookingStatus.isStatus();

        if (currentAvailable == null || currentAvailable != newStatus) {
            item.setAvailable(newStatus);
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
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(searchText)) ||
                                (item.getDescription() != null && item.getDescription()
                                        .toLowerCase().contains(searchText))
                )
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDTO> searchAllItemOfOwnerById(Long ownerId) {
        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getOwner().equals(ownerId))
                .map(itemMapper::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }
}