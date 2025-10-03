package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    @NotBlank
    @Size(max = 200, message = "Комментарий не может превышать 200 символов.")
    private String comment;

    @NotNull
    Long itemId;

    @NotNull
    private Long userId;

    @NotNull
    LocalDateTime date;
}
