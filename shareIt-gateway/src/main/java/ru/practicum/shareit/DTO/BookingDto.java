package ru.practicum.shareit.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Boo status;

    private SimpleItemDto item;

    private SimpleUserDto booker;
}