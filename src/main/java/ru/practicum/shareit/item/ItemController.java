package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item createItem(@Valid @RequestBody Item item,
                           @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemService.createItem(id, item);
    }

    @GetMapping("/{id}")
    public ItemDTO getItemDTOById(@PathVariable @Min(1) Long id) {
        return itemService.getItemDTOById(id);
    }

    @GetMapping
    public Collection<ItemDTO> searchAllItemOfOwnerById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.searchAllItemOfOwnerById(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDTO> searchItemDTOByText(
            @RequestParam("text")
            String text) {
        return itemService.searchItemDtoByText(text);
    }

    @PatchMapping("/{id}")
    public Item updateItem(@PathVariable @Min(1) Long id,
                           @RequestBody Item item, // Убрали @Valid
                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.updateItem(id, owner, item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable
                           @Min(1) Long id,
                           @RequestHeader("X-Sharer-User-Id") Long owner) {
        itemService.deleteItem(owner, id);
    }
}

