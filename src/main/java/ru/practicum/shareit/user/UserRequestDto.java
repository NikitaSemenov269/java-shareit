package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Имя не может быть пустым полем.")
    private String name;

    @NotBlank
    @Email(message = "Некорректный формат email.")
    private String email;
}
