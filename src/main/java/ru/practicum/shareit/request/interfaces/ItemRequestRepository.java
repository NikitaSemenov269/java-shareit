package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepository {

    void save(ItemRequest itemRequest);

    ItemRequest findById(Long itemRequestId);

    List<ItemRequest> findByRequesterId(Long requesterId);

    List<ItemRequest> findAll();

    List<ItemRequest> findAllExceptRequester(Long requesterId);
}
