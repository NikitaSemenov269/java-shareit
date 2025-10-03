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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestValidation itemRequestValidation;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest) {

        log.info("Попытка создания новой заявки.");

        ItemRequest newItemRequest = itemRequestRepository.save(itemRequest);

        log.info("Создана новая заявка.");
        return ;
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
