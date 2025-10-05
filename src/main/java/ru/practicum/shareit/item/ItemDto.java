package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static ru.practicum.shareit.enums.BookingStatus.AWAITING_A_REQUEST;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустой строкой.")
    @Size(max = 45, message = "Название вещи не может превышать 45 символов.")
    private String name;

    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String description;

    @NotNull(message = "Статус не может быть null.")
    private Boolean available = AWAITING_A_REQUEST.isStatus(); // true
}
