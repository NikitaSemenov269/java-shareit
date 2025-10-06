package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(RequestDto requestDto, Long userId);

    RequestDto getRequestById(Long requestId, Long userId);

    List<RequestDto> getUserRequests(Long userId);

    List<RequestDto> getAllRequests(Long userId);

    List<RequestDto> getOtherUserRequests(Long userId, Integer from, Integer size);
}