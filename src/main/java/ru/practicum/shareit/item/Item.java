package ru.practicum.shareit.item;

import jakarta.validation.constraints.*;
import lombok.*;

import static ru.practicum.shareit.enums.BookingStatus.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Min(value = 1, message = "Id вещи должно быть положительным числом.")
    private Long id;

    @NotBlank(message = "Название не может быть пустой строкой.")
    @Size(max = 45, message = "Название вещи не может превышать 45 символов.")
    private String name;

    @Min(value = 1, message = "Id владельца должно быть положительным числом.")
    private Long owner;

    @NotBlank
    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String description;

    private Boolean available;

    //Ссылка на запрос аренды
    private Long request;
}
