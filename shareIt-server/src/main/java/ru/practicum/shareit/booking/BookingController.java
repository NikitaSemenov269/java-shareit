package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.enums.State;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {

        try {
            BookingDto bookingDto = bookingService.createBooking(bookerId, bookingRequestDto);
            return ResponseEntity.ok(bookingDto);

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ValidationException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateAvailableStatusBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(bookingService.updateAvailableStatusBooking(ownerId, bookingId, approved));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> canceledBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        bookingService.canceledBookingById(bookerId, bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long bookingId,
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