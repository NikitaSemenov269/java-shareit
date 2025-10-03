/*
package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "item_request")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "description", nullable = false)
    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String descriptionRequest;

    @NotNull
    @Column(name = "requester", nullable = false)
    private User requester;

    @Column(name = "created_date")
    private LocalDateTime created;
}
*/
