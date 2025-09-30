package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingDto  {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long id;

    @NotBlank(message = "Название не может быть пустой строкой.")
    @Size(max = 45, message = "Название вещи не может превышать 45 символов.")
    private String name;

    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String description;

    @NotNull(message = "Статус не может быть null.")
    private Boolean available;

    @NotNull(message = "Дата начала аренды не может быть null.")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания аренды не может быть null.")
    private LocalDateTime end;
}
