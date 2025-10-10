package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.*;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long ownerId, ItemRequestDto itemRequestDto);

    ItemDto updateItem(Long itemId, Long ownerId, ItemRequestDto itemRequestDto);

    void deleteItem(Long ownerId, Long itemId);

    ItemWithBookingAndCommentsDto getItemById(Long itemId, Long userId);

    Collection<ItemDto> searchItemDtoByText(String text);

    Collection<ItemDto> searchAllItemOfOwnerById(Long ownerId);

    void updateItemAvailable(Long itemId, Boolean bookingStatus);

    CommentDto addComment(Long userId, Long itemId, String comment);
}
