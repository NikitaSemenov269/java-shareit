package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class ItemDTO {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long itemId;

    @NotBlank(message = "Название не может быть пустой строкой.")
    @Size(max = 45, message = "Название вещи не может превышать 45 символов.")
    private String title;

    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String description;
}
