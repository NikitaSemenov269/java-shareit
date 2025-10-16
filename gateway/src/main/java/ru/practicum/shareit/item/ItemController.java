package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.*;
import ru.practicum.shareit.GatewayServiceClient;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final GatewayServiceClient gatewayServiceClient;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(gatewayServiceClient.createItem(itemRequestDto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemWithBookingAndCommentsDto> getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(gatewayServiceClient.getItemById(id, userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> searchAllItemOfOwnerById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(gatewayServiceClient.searchAllItemOfOwnerById(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItemDTOByText(
            @RequestParam("text") String text) {
        return ResponseEntity.ok().body(gatewayServiceClient.searchItemDtoByText(text));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long owner) {
        return ResponseEntity.ok().body(gatewayServiceClient.updateItem(id, itemRequestDto, owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long owner) {
        gatewayServiceClient.deleteItem(id, owner);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CommentTextDto commentTextDto) {
        return ResponseEntity.ok().body(gatewayServiceClient.addComment(itemId, userId, commentTextDto));
    }
}
