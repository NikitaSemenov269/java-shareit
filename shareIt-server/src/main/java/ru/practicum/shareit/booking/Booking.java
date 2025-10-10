package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Дата начала аренды не может быть null.")
    @Column(name = "start_rent", nullable = false)
    private LocalDateTime start;

    @NotNull(message = "Дата окончания аренды не может быть null.")
    @Column(name = "end_rent", nullable = false)
    private LocalDateTime end;

    @NotNull(message = "Статус бронирования обязательное поле.")
    @Column(name = "status", nullable = false)
    private BookingStatus status = WAITING; // true

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
}
