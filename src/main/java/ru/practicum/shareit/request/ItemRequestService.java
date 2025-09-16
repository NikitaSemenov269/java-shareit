package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfacesItem.ItemServiceInterface;
import ru.practicum.shareit.request.interfacesRequest.ItemRequestRepositoryInterface;
import ru.practicum.shareit.request.interfacesRequest.ItemRequestServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService implements ItemRequestServiceInterface {
    ItemRequestValidation itemRequestValidation;
    ItemRequestRepositoryInterface itemRequestRepositoryInterface;


    private final ItemServiceInterface itemServiceInterface;
    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest) {
        itemRequest.setRequestId(counter.getAndIncrement());
        Long id = itemRequest.getRequestId();
        log.info("Попытка создания новой заявки с ID: {}", id);

        itemRequestValidation.itemRequestValidationById(id);

        itemRequestRepositoryInterface.save(itemRequest);
        log.info("Создана новая заявка c ID: {} ", id);
        return itemRequest;
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        log.info("Попытка получения заявки по ID: {}", requestId);

        itemRequestValidation.itemRequestValidationById(requestId);

        return Optional.ofNullable(itemRequestRepositoryInterface.findById(requestId))
                .orElseThrow(() -> new NotFoundException("Заявка с ID: " + requestId + " не найдена."));
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        log.info("Попытка получения заявки пользователя с ID: {}", userId);

        itemRequestValidation.itemRequestValidationById(userId);

        return itemRequestRepositoryInterface.findByRequesterId(userId);
    }

    @Override
    public List<ItemRequest> getAllRequests() {
        log.info("Попытка получения всех заявок пользователей");

        return itemRequestRepositoryInterface.findAll();
    }

    @Override
    public List<ItemRequest> getOtherUserRequests(Long userId) {
        log.info("Попытка получения заявок пользователей кроме пользователя с ID: {}", userId);

        itemRequestValidation.itemRequestValidationById(userId);

        return itemRequestRepositoryInterface.findAllExceptRequester(userId);
    }
}
