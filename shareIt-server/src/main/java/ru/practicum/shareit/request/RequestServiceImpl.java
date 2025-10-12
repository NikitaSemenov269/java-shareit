package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.DTO.ItemDtoForRequester;
import ru.practicum.shareit.item.interfaces.ItemMapper;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.interfaces.RequestMapper;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
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
    private final ItemMapper itemMapper;

    @Transactional
    @Override
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

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с ID: " + requestId + " не найдена."));

        Collection<ItemDtoForRequester> items = itemRepository.findByRequest(request)
                .stream()
                .map(itemMapper::toDtoForRequest)
                .collect(Collectors.toList());

        ResponseRequestDto responseRequestDto = mapper.toDto(request);
        responseRequestDto.setItems(items);

        return responseRequestDto;
    }

    @Override
    public Collection<ResponseRequestDto> getUserRequests(Long userId) {
        log.info("Попытка получения заявок пользователя с ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }

        Collection<Request> requests = requestRepository.findByRequesterId(userId);

        Collection<Item> items = itemRepository.findByRequestIn(requests);

        return builderResponseRequestDtos(requests, items);
    }

    @Override
    public Collection<ResponseRequestDto> getOtherUserRequests(Long userId, Integer from, Integer size) {
        log.info("Попытка получения заявок других пользователей для пользователя ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден.");
        }

        Collection<Request> requestsExceptUser = requestRepository.findAllRequestsExceptUser(userId);

        Collection<Item> items = itemRepository.findByRequestIn(requestsExceptUser);

        return builderResponseRequestDtos(requestsExceptUser, items);
    }

    private Collection<ResponseRequestDto> builderResponseRequestDtos(Collection<Request> requests,
                                                                      Collection<Item> items) {
        return requests.stream()
                .map(request -> {
                    Collection<ItemDtoForRequester> itemDtos = items.stream()
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
}