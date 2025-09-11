package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class Booking {
    @Min(value = 1, message = "Id заявки должно быть положительным числом.")
    private Long bookingId;

    @NotNull
    @Min(value = 1, message = "Id вещи должно быть положительным числом.")
    private Long itemId;

    @NotNull
    @Min(value = 1, message = "Id арендатора должно быть положительным числом.")
    private Long bookerId;

    @NotNull(message = "Дата начала аренды не может быть null.")
    private LocalDate startRent;

    @NotNull(message = "Дата окончания аренды не может быть null.")
    private LocalDate endRent;

    @NotNull(message = "Статус бронирования обязательное поле.")
    private BookingStatus availableItem;
}
