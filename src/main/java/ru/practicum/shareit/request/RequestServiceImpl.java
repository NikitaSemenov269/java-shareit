package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoForRequester;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.interfaces.RequestMapper;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public ResponseRequestDto createRequest(RequestDto requestDto, Long userId) {
        log.info("Попытка создания новой заявки пользователем ID: {}", userId);

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        Request request = new Request();
        request.setDescriptionRequest(requestDto.getDescriptionRequest());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        Request newRequest = requestRepository.save(request);
        log.info("Создана новая заявка с ID: {}", newRequest.getId());

        return mapper.toDto(newRequest);
    }

    @Override
    public ResponseRequestDto getRequestById(Long requestId, Long userId) {
        log.info("Попытка получения заявки по ID: {} пользователем ID: {}", requestId, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с ID: " + requestId + " не найдена."));

        return mapper.toDto(request);
    }

    @Override
    public Collection<ResponseRequestDto> getUserRequests(Long userId) {
        log.info("Попытка получения заявок пользователя с ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        Collection<Request> requests = requestRepository.findByRequesterId(userId);

        Collection<Item> items = itemRepository.findByRequestIn(requests);

        return requests.stream()
                .map(request -> {
                    List<ItemDtoForRequester> itemDtos = items.stream()
                            .filter(item -> item.getRequest().getId().equals(request.getId()))
                            .map(item ->
                                    new ItemDtoForRequester(item.getId(), item.getName(), item.getOwner().getId()))
                            .collect(Collectors.toList());

                    return new ResponseRequestDto(
                            request.getId(),
                            request.getRequester().getId(),
                            request.getDescriptionRequest(),
                            request.getCreated(),
                            itemDtos
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    public Collection<ResponseRequestDto> getAllRequests(Long userId) {
        log.info("Попытка получения всех заявок пользователем ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        return requestRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ResponseRequestDto> getOtherUserRequests(Long userId, Integer from, Integer size) {
        log.info("Попытка получения заявок других пользователей для пользователя ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        return
    }

    @Override
    public ResponseRequestDto updateItemOfRequest(Long requestId, ItemDto itemDto) {
        log.info("Попытка предложения вещи для аренды по заявке с ID: {}", requestId);

        return null;
    }
}