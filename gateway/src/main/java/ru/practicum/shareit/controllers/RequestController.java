package ru.practicum.shareit.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.GatewayServiceClient;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final GatewayServiceClient gatewayServiceClient;

    @PostMapping
    public ResponseEntity<ResponseRequestDto> createRequest(
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(gatewayServiceClient.createRequest(requestDto, userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ResponseRequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(gatewayServiceClient.getUserRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ResponseRequestDto> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(gatewayServiceClient.getRequestById(requestId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ResponseRequestDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(gatewayServiceClient.getAllRequests(userId, from, size));
    }
}

