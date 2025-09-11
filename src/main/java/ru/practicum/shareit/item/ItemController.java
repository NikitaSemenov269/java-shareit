package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public Item createItem(@Valid
                           @RequestBody Item item,
                           @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return itemService.createItem(ownerId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDTO getItemDTOById(@PathVariable
                                  @Min(1) Long itemId) {
        return itemService.getItemDTOById(itemId);
    }

    @GetMapping
    public Collection<ItemDTO> searchAllItemOfOwnerById(
            @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return itemService.searchAllItemOfOwnerById(ownerId);
    }

    @GetMapping("/items/search?text={text}")
    public Collection<ItemDTO> searchItemDTOByText(
            @NotBlank(message = "Поиск по ключевым словам должен содержать минимум 1 символ")
            @Min(1)
            @PathVariable("text") String text) {
        return itemService.searchItemDtoByText(text);
    }

    @PatchMapping
    public Item updateItem(@Valid
                           @RequestBody Item updateItem,
                           @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return itemService.updateItem(ownerId, updateItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable
                           @Min(1) Long itemId,
                           @RequestHeader("X-Owner-User-Id") Long ownerId) {
        itemService.deleteItem(ownerId, itemId);
    }
}

