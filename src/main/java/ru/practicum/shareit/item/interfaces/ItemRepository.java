package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemWithBookingDto;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT new ItemDto(i.id, i.name, i.description, i.available) " +
            "FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    Collection<ItemDto> findAllByText(@Param("text") String text);

    @Query("SELECT new ItemWithBookingDto(i.id, i.name, i.description, i.available) " +
            "FROM Item i " +
            "WHERE i.owner.id = :ownerId")
    Collection<ItemWithBookingDto> findAllByOwnerId(@Param("ownerId") Long ownerId);
}
