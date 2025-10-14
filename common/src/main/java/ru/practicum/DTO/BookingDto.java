package ru.practicum.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.BookingStatus;

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

    private SimpleItemDto item;

    private SimpleUserDto booker;
}