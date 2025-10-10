package ru.practicum.shareit.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.State;
import ru.practicum.shareit.GatewayServiceClient;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final GatewayServiceClient gatewayServiceClient;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return ResponseEntity.ok().body(gatewayServiceClient.createBooking(bookingRequestDto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateAvailableStatusBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(gatewayServiceClient.updateAvailableStatusBooking(ownerId, approved, bookingId));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> canceledBookingById(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        gatewayServiceClient.canceledBookingById(bookerId, bookingId);
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
