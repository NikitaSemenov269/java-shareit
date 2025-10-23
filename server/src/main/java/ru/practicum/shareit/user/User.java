package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустой строкой.")
    @Length(max = 30, message = "Имя пользователя не может быть больше 30 символов.")
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @NotBlank(message = "Email не может быть пустой строкой.")
    @Email(message = "Некорректный формат email.")
    @Column(name = "email", nullable = false, length = 45, unique = true)
    private String email;
}
