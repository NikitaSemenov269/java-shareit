package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item createItem(@Valid @RequestBody Item item,
                           @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemDTOById(@PathVariable @Min(1) Long itemId) {
        return itemService.getItemDTOById(itemId);
    }

    @PatchMapping
    public Item updateItem(@Valid @RequestBody Item updateItem,
                           @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return itemService.updateItem(ownerId, updateItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable @Min(1) Long itemId,
                           @RequestHeader("X-Owner-User-Id") Long ownerId) {
        itemService.deleteItem(ownerId, itemId);
    }
}

