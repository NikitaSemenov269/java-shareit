package ru.practicum.shareit.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.shareit.user.User;

import static ru.practicum.shareit.enums.BookingStatus.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название не может быть пустой строкой.")
    @Size(max = 45, message = "Название вещи не может превышать 45 символов.")
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @NotBlank
    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @NotNull(message = "Статус бронирования обязательное поле.")
    @Column(name = "available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;*/
}
