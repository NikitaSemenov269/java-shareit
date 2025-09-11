package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class Booking {
    private Long id;
    private Long itemId;
    private Long bookerId;
    private LocalDate startRent;
    private LocalDate endRent;
    private String status;
}
