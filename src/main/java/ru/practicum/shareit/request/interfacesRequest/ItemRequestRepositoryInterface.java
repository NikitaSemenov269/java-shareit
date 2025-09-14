package ru.practicum.shareit.request.interfacesRequest;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestRepositoryInterface {

    void save(ItemRequest itemRequest);

    ItemRequest findById(Long itemRequestId);

    List<ItemRequest> findByRequesterId(Long requesterId);

    List<ItemRequest> findAll();

    List<ItemRequest> findAllExceptRequester(Long requesterId);
}
