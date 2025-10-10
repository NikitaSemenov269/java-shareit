package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.DTO.ItemDtoForRequester;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "description", nullable = false)
    @Size(max = 200, message = "Описание вещи не может превышать 200 символов.")
    private String descriptionRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull
    @Column(name = "created_date")
    private LocalDateTime created;

    private Collection<ItemDtoForRequester> items;
}