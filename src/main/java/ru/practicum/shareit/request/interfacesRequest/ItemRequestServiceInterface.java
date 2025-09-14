package ru.practicum.shareit.request.interfacesRequest;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestServiceInterface {

    ItemRequest createRequest(ItemRequest itemRequest);

    ItemRequest getRequestById(Long requestId);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests();

    List<ItemRequest> getOtherUserRequests(Long userId);
}
