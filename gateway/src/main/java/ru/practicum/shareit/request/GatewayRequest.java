package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.GatewayServiceClient;
import ru.practicum.shareit.Validation;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayRequest {

    private final Validation validation;
    private final GatewayServiceClient gatewayServiceClient;

    public ResponseEntity<ResponseRequestDto> createRequest(RequestDto requestDto, Long userId) {
        log.info("Запрос на добавление запроса от пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.createRequest(requestDto, userId));
    }

    public ResponseEntity<Collection<ResponseRequestDto>> getUserRequests(Long userId) {
        log.info("Запрос на получение запросов пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.getUserRequests(userId));
    }

    public ResponseEntity<ResponseRequestDto> getRequestById(Long requestId, Long userId) {
        log.info("Запрос на получение запроса с ID: {} от пользователя с ID: {}", userId, requestId);
        validation.userIdValidation(userId);
        validation.requestIdValidation(requestId);

        return ResponseEntity.ok().body(gatewayServiceClient.getRequestById(requestId, userId));
    }

    public ResponseEntity<Collection<ResponseRequestDto>> getOtherUserRequests(Long userId, Integer from, Integer size) {
        log.info("Запрос на получение всех запросов кроме пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.getAllRequests(userId, from, size));
    }

}
