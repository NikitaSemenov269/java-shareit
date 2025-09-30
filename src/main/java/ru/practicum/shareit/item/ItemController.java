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
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody Item item,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.createItem(userId, item));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemWithBookingDto> getItemDTOById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok().body(itemService.getItemDTOById(id));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemWithBookingDto>> searchAllItemOfOwnerById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(itemService.searchAllItemOfOwnerById(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItemDTOByText(
            @RequestParam("text")
            String text) {
        return ResponseEntity.ok().body(itemService.searchItemDtoByText(text));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable @Min(1) Long id,
                                               @RequestBody Item item,
                                               @RequestHeader("X-Sharer-User-Id") Long owner) {
        return ResponseEntity.ok().body(itemService.updateItem(id, owner, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable
                                           @Min(1) Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        itemService.deleteItem(owner, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}

