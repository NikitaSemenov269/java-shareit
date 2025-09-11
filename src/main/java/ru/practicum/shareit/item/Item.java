package ru.practicum.shareit.item;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;
import static ru.practicum.shareit.enums.BookingStatus.*;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Item {
    @Min(value = 1, message = "Id вещи должно быть положительным числом.")
    private Long itemId;

    @NotNull
    @Min(value = 1, message = "Id владельца должно быть положительным числом.")
    private Long ownerId;

    @NotBlank(message = "Название не может быть пустой строкой.")
    @Size(max = 45, message = "Название вещи не может превышать 45 символов.")
    private String title;

    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String description;

    //статус подтверждения брони
    //AWAITING_A_REQUEST("Номинальный статус бронирования", true)
    private BookingStatus available = AWAITING_A_REQUEST;

    //Ссылка на запрос аренды
    private Long request;
}
