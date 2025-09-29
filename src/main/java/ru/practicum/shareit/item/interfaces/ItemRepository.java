package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {


   /* void addItem(Item item);

    ItemDto getItemDTOById(Long itemId);

    Item updateItem(Item updateItem);

    void deleteItemById(Long itemId);

    boolean existsByItemId(Long itemId);

    Collection<ItemDto> searchItemDtoByText(String text);

    Collection<ItemDto> searchAllItemOfOwnerById(Long ownerId);

    Item updateItemAvailable(Long itemId, BookingStatus bookingStatus);

    Item getItemById(Long itemId);*/
}
