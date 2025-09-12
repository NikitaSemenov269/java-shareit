package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Min(value = 1, message = "Id должно быть положительным числом.")
    private Long id;

    @NotBlank(message = "Имя не может быть пустым полем.")
    private String name;

    @NotBlank
    @Email(message = "Некорректный формат email.")
    private String email;
}
