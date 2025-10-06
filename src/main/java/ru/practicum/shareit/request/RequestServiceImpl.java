package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.interfaces.RequestMapper;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.request.interfaces.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public RequestDto createRequest(RequestDto requestDto, Long userId) {
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
    public RequestDto getRequestById(Long requestId, Long userId) {
        log.info("Попытка получения заявки по ID: {} пользователем ID: {}", requestId, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с ID: " + requestId + " не найдена."));

        return mapper.toDto(request);
    }

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        log.info("Попытка получения заявок пользователя с ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        return requestRepository.findByRequesterId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAllRequests(Long userId) {
        log.info("Попытка получения всех заявок пользователем ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        return requestRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getOtherUserRequests(Long userId, Integer from, Integer size) {
        log.info("Попытка получения заявок других пользователей для пользователя ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден."));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        return requestRepository.findAllExceptRequester(userId, pageable).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}