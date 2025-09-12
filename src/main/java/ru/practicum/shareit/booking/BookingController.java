package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.enums.BookingStatus;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@Valid
                                 @RequestBody Booking booking,
                                 @RequestHeader("X-Booker-User-Id") Long bookerId) {
        return bookingService.createBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBooking(@PathVariable
                                 @Min(1) Long bookingId,
                                 @Valid
                                 @RequestBody Booking booking,
                                 @RequestHeader("X-Booker-User-Id") Long bookerId) {
        return bookingService.updateBooking(bookingId, bookerId, booking);
    }

    @PatchMapping("/status/{bookingId}")
    public Booking updateAvailableStatusBooking(@PathVariable
                                                @Min(1) Long bookingId,
                                                @RequestBody BookingStatus bookingStatus,
                                                @RequestHeader("X-Owner-User-Id") Long ownerId) {
        return bookingService.updateAvailableStatusBooking(ownerId, bookingId, bookingStatus);
    }

    @PatchMapping("/cancel/{bookingId}")
    public void canceledBookingById(@PathVariable
                                    @Min(1) Long bookingId,
                                    @RequestHeader("X-Booker-User-Id") Long bookerId) {
        bookingService.canceledBookingById(bookerId, bookingId);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteBookingById(@PathVariable
                                  @Min(1) Long bookingId,
                                  @RequestHeader("X-Owner-User-Id") Long bookerId) {
        bookingService.deleteBooking(bookerId, bookingId);
    }
}