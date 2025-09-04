package ru.practicum.shareit.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDTO {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long idUser;

    @NotBlank(message = "Логин не может быть пустым полем.")
    private String login;
}
