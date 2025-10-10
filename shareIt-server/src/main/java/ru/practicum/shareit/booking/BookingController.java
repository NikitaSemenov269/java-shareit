package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.enums.State;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return ResponseEntity.ok().body(bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateAvailableStatusBooking(
            @PathVariable("bookingId") @Min(1) Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(bookingService.updateAvailableStatusBooking(ownerId, bookingId, approved));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> canceledBookingById(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        bookingService.canceledBookingById(bookerId, bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getAllBookingByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity.ok().body(bookingService.getAllBookingByBookerId(bookerId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getAllBookingByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity.ok().body(bookingService.getAllBookingByOwnerId(ownerId, state));
    }
}