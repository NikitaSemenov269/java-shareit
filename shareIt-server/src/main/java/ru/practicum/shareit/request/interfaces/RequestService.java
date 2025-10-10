package ru.practicum.shareit.request.interfaces;

import ru.practicum.shareit.request.RequestDto;
import ru.practicum.shareit.request.ResponseRequestDto;

import java.util.Collection;

public interface RequestService {

    ResponseRequestDto createRequest(RequestDto requestDto, Long userId);

    ResponseRequestDto getRequestById(Long requestId, Long userId);

    Collection<ResponseRequestDto> getUserRequests(Long userId);

    Collection<ResponseRequestDto> getOtherUserRequests(Long userId, Integer from, Integer size);

}