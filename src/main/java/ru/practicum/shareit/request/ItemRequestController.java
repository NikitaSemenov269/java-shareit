package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.interfaces.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequest> createRequest(@Valid @RequestBody ItemRequest itemRequest) {
         return ResponseEntity.ok().body(itemRequestService.createRequest(itemRequest));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequest> getRequestById(@PathVariable @Min(1) Long requestId) {
        return ResponseEntity.ok().body(itemRequestService.getRequestById(requestId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequest>> getUserRequests(@RequestParam Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequest>> getAllRequests() {
        return ResponseEntity.ok().body(itemRequestService.getAllRequests());
    }

    @GetMapping("/other/{userId}")
    public ResponseEntity<List<ItemRequest>> getOtherUserRequests(Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getOtherUserRequests(userId));
    }
}