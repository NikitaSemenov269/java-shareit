package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Дата начала аренды не может быть null.")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания аренды не может быть null.")
    private LocalDateTime end;

    @NotNull(message = "Статус бронирования обязательное поле.")
    private BookingStatus status;

    @NotNull
    private Long itemId;

    @NotNull
    private Long bookerId;
}
