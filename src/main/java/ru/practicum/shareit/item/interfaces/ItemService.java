package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.*;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long ownerId, Item newItem);

    ItemDto updateItem(Long itemId, Long ownerId, Item updateItem);

    void deleteItem(Long ownerId, Long itemId);

    ItemWithCommentsDto getItemById(Long itemId);

    Collection<ItemDto> searchItemDtoByText(String text);

    Collection<ItemWithBookingAndCommentsDto> searchAllItemOfOwnerById(Long ownerId);

    void updateItemAvailable(Long itemId, Boolean bookingStatus);

    CommentDto addNewComment(Long userId, Long itemId, String comment);
}
