package ru.practicum.shareit.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class UserDTO {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long idUser;

    @NotBlank(message = "Имя не может быть пустым полем.")
    private String name;
}
