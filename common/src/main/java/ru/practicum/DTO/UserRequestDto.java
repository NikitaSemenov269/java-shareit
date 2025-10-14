package ru.practicum.DTO;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    private String name;

    @Email(message = "Некорректный формат email.")
    private String email;
}
