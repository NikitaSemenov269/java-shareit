package ru.practicum.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotBlank
    @Size(max = 200, message = "Комментарий не может превышать 200 символов.")
    private String text;

    @NotNull
    private Long itemId;

    @NotNull
    private Long userId;
}
