package ru.practicum.shareit.booking;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        try {
            return gatewayServiceClient.createBooking(bookingRequestDto, bookerId);

        } catch (FeignException.NotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateAvailableStatusBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(gatewayServiceClient.updateAvailableStatusBooking(bookingId, approved, ownerId));
    }

    @PatchMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> canceledBookingById(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        gatewayServiceClient.canceledBookingById(bookingId, bookerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable @Min(1) Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(gatewayServiceClient.getBookingById(bookingId, userId));
    }


    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getAllBookingByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity.ok().body(gatewayServiceClient.getAllBookingByBookerId(bookerId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getAllBookingByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") State state) {
        return ResponseEntity.ok().body(gatewayServiceClient.getAllBookingByOwnerId(ownerId, state));
    }
}
