package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ResponseRequestDto> createRequest(
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(requestService.createRequest(requestDto, userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ResponseRequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(requestService.getUserRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ResponseRequestDto> getRequestById(
            @PathVariable @Min(1) Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(requestService.getRequestById(requestId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ResponseRequestDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(requestService.getOtherUserRequests(userId, from, size));
    }
}