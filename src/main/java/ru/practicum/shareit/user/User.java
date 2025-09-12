package ru.practicum.shareit.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long id;

    @NotBlank(message = "Имя не может быть пустой строкой.")
    @Size(max = 30, message = "Имя пользователя не может быть больше 30 символов.")
    private String name;

    @NotBlank
    @Email(message = "Некорректный формат email.")
    private String email;
}
