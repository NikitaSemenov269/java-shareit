package ru.practicum.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.BookingStatus;

import java.time.LocalDateTime;

import static ru.practicum.enums.BookingStatus.WAITING;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private BookingStatus status = WAITING;
}
