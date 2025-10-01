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


    @Query(nativeQuery = true, value =
            "SELECT i.id, i.name, i.description, i.available, " +
                    "last_b.start_rent as lastStart, last_b.end_rent as lastEnd, " +
                    "next_b.start_rent as nextStart, next_b.end_rent as nextEnd " +
                    "FROM items i " +
                    "LEFT JOIN LATERAL (" +
                    "  SELECT start_rent, end_rent FROM bookings " +
                    "  WHERE item_id = i.id AND end_rent < NOW() AND status = 'APPROVED' " +
                    "  ORDER BY end_rent DESC LIMIT 1" +
                    ") last_b ON true " +
                    "LEFT JOIN LATERAL (" +
                    "  SELECT start_rent, end_rent FROM bookings " +
                    "  WHERE item_id = i.id AND start_rent > NOW() AND status = 'APPROVED' " +
                    "  ORDER BY start_rent ASC LIMIT 1" +
                    ") next_b ON true " +
                    "WHERE i.owner_id = ?1")
    Collection<ItemWithBookingDto> findByOwnerIdWithBookings(Long ownerId);
}

