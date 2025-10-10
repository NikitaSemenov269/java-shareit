package ru.practicum.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRequestDto {
    private Long id;

    private Long requesterId;

    private String descriptionRequest;

    private LocalDateTime created;

    private Collection<ItemDtoForRequester> items;
}
