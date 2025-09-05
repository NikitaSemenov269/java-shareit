package ru.practicum.shareit.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class User {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long userId;

    @NotBlank(message = "Имя не может быть пустой строкой.")
    @Size(max = 30, message = "Имя пользователя не может быть больше 30 символов.")
    private String name;

//    @NotBlank(message = "Логин не может быть пустым полем.")
//    private String login;

    @NotBlank(message = "Email не может быть пустой строкой.")
    @Email(message = "Некорректный формат email.")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым полем.")
    private String password;
}
