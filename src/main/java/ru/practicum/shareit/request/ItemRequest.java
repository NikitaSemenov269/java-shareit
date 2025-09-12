package ru.practicum.shareit.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    private Long idRequest;
    private String descriptionRequest;
    private Long requestorId;
    private LocalDateTime created;
}
