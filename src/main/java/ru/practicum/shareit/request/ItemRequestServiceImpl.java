package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestValidation itemRequestValidation;
    ItemRequestRepository itemRequestRepository;


    private final ItemService itemServiceInterface;
    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest) {
        itemRequest.setRequestId(counter.getAndIncrement());
        Long id = itemRequest.getRequestId();
        log.info("Попытка создания новой заявки с ID: {}", id);

        itemRequestValidation.itemRequestValidationById(id);

        itemRequestRepository.save(itemRequest);
        log.info("Создана новая заявка c ID: {} ", id);
        return itemRequest;
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        log.info("Попытка получения заявки по ID: {}", requestId);

        itemRequestValidation.itemRequestValidationById(requestId);

        return Optional.ofNullable(itemRequestRepository.findById(requestId))
                .orElseThrow(() -> new NotFoundException("Заявка с ID: " + requestId + " не найдена."));
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        log.info("Попытка получения заявки пользователя с ID: {}", userId);

        itemRequestValidation.itemRequestValidationById(userId);

        return itemRequestRepository.findByRequesterId(userId);
    }

    @Override
    public List<ItemRequest> getAllRequests() {
        log.info("Попытка получения всех заявок пользователей");

        return itemRequestRepository.findAll();
    }

    @Override
    public List<ItemRequest> getOtherUserRequests(Long userId) {
        log.info("Попытка получения заявок пользователей кроме пользователя с ID: {}", userId);

        itemRequestValidation.itemRequestValidationById(userId);

        return itemRequestRepository.findAllExceptRequester(userId);
    }
}
