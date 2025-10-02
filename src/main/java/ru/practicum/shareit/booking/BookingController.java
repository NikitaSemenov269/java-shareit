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
    public ResponseEntity<Booking> createBooking(@Valid
                                                 @RequestBody BookingRequestDto bookingRequestDto,
                                                 @RequestHeader("X-Owner-User-Id") Long bookerId) {
        return ResponseEntity.ok().body(bookingService.createBooking(bookerId, bookingRequestDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBooking(@PathVariable
                                                 @Min(1) Long bookingId,
                                                 @Valid
                                                 @RequestBody Booking booking,
                                                 @RequestHeader("X-Owner-User-Id") Long bookerId) {
        return ResponseEntity.ok().body(bookingService.updateBooking(bookingId, bookerId, booking));
    }

    @PatchMapping("/status/{bookingId}")
    public ResponseEntity<Booking> updateAvailableStatusBooking(@PathVariable
                                                                @Min(1) Long bookingId,
                                                                @RequestBody boolean bookingStatus,
                                                                @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(bookingService.updateAvailableStatusBooking(ownerId, bookingId, bookingStatus));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<BookingDto> canceledBookingById(@PathVariable
                                                    @Min(1) Long bookingId,
                                                    @RequestHeader("X-Booker-User-Id") Long bookerId) {
        bookingService.canceledBookingById(bookerId, bookingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable
                                                  @Min(1) Long bookingId,
                                                  @RequestHeader("X-Booker-Or-Owner-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(userId, bookingId));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBookingById(@PathVariable
                                                  @Min(1) Long bookingId,
                                                  @RequestHeader("X-Owner-User-Id") Long bookerId) {
        bookingService.deleteBooking(bookerId, bookingId);
        return ResponseEntity.noContent().build();
    }
}