package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.interfacesRequest.ItemRequestRepositoryInterface;
import ru.practicum.shareit.request.interfacesRequest.ItemRequestServiceInterface;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService implements ItemRequestServiceInterface {
    ItemRequestValidation itemRequestValidation;
    ItemRequestRepositoryInterface itemRequestRepositoryInterface;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest) {
        itemRequest.setRequestId(counter.getAndIncrement());
        Long id = itemRequest.getRequestId();
        log.info("Попытка создания новой заявки с ID: {}",  id);

        itemRequestValidation.itemRequestValidationById(id);

        itemRequestRepositoryInterface.save(itemRequest);
        log.info("Создана новая заявка c ID: {} ", id);
        return itemRequest;
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        return null;
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        return List.of();
    }

    @Override
    public List<ItemRequest> getAllRequests() {
        return List.of();
    }

    @Override
    public List<ItemRequest> getOtherUserRequests(Long userId) {
        return List.of();
    }
}
