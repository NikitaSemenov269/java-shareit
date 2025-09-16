package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest createRequest(@Valid @RequestBody ItemRequest itemRequest) {
        return itemRequestService.createRequest(itemRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequest getRequestById(@PathVariable @Min(1) Long requestId) {
        return itemRequestService.getRequestById(requestId);
    }

    @GetMapping
    public List<ItemRequest> getUserRequests(@RequestParam Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequest> getAllRequests() {
        return itemRequestService.getAllRequests();
    }

    @GetMapping("/other/{userId}")
    public List<ItemRequest> getOtherUserRequests(Long userId) {
        return itemRequestService.getOtherUserRequests(userId);
    }
}