package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.interfaces.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @Valid
            @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader("X-Booker-User-Id") Long id) {
        return ResponseEntity.ok().body(bookingService.createBooking(id, bookingRequestDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateAvailableStatusBooking(
            @PathVariable("bookingId") @Min(1) Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(bookingService.updateAvailableStatusBooking(ownerId, bookingId, approved));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> canceledBookingById(
            @PathVariable
            @Min(1) Long bookingId,
            @RequestHeader("X-Booker-User-Id") Long bookerId) {
        bookingService.canceledBookingById(bookerId, bookingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable
            @Min(1) Long bookingId,
            @RequestHeader("X-Booker-Or-Owner-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(userId, bookingId));
    }
}
