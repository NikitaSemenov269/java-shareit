package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.request.interfacesRequest.ItemRequestRepositoryInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRequestRepository implements ItemRequestRepositoryInterface {
    private Map<Long, ItemRequest> itemRequests = new HashMap<>();

    @Override
    public void save(ItemRequest itemRequest) {
        itemRequests.put(itemRequest.getRequestId(), itemRequest);
    }

    @Override
    public ItemRequest findById(Long itemRequestId) {
        return itemRequests.get(itemRequestId);
    }

    @Override
    public List<ItemRequest> findByRequesterId(Long requesterId) {
        return itemRequests.values().stream()
                .filter(itemRequest -> itemRequest.getRequestId().equals(requesterId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequest> findAll() {
        return itemRequests.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<ItemRequest> findAllExceptRequester(Long requesterId) {
        return itemRequests.values().stream()
                .filter(itemRequest -> !itemRequest.getRequesterId().equals(requesterId))
                .collect(Collectors.toList());
    }
}
