package ru.practicum.shareit.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private Long requestId;

    @NotBlank
    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String descriptionRequest;

    @Min(value = 1, message = "Id владельца должно быть положительным числом.")
    private Long requesterId;

    private LocalDateTime created;
}
