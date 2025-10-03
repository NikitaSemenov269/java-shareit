package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.createItem(userId, itemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemWithCommentsDto> getItemById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok().body(itemService.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemWithBookingAndCommentsDto>> searchAllItemOfOwnerById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(itemService.searchAllItemOfOwnerById(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItemDTOByText(
            @RequestParam("text") String text) {
        return ResponseEntity.ok().body(itemService.searchItemDtoByText(text));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable @Min(1) Long id,
                                              @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long owner) {
        return ResponseEntity.ok().body(itemService.updateItem(id, owner, itemDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable @Min(1) Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        itemService.deleteItem(owner, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable @Min(1) Long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody String comment) {
        return ResponseEntity.ok().body(itemService.addNewComment(userId, itemId, comment));
    }
}
