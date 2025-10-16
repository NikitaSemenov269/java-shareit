package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.GatewayServiceClient;
import ru.practicum.shareit.Validation;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayBooking {

    private final Validation validation;
    private final GatewayServiceClient gatewayServiceClient;

    public ResponseEntity<BookingDto> createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        try {
            ResponseEntity<BookingDto> booking = gatewayServiceClient.createBooking(bookingRequestDto, bookerId);
            return booking;

        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<BookingDto> updateAvailableStatusBooking(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Запрос на обновление статуса бронирования с ID: {} от пользователя с ID: {}", bookingId, ownerId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(ownerId);

        if (approved == null) {
            throw new ValidationException("Статус не может быть null.");
        }

        return ResponseEntity.ok().body(gatewayServiceClient.updateAvailableStatusBooking(bookingId, approved, ownerId));
    }

    public ResponseEntity<Void> canceledBookingById(Long bookingId, Long bookerId) {
        log.info("Запрос на закрытие бронирования с ID: {} от пользователя с ID: {}", bookingId, bookerId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(bookerId);

        gatewayServiceClient.canceledBookingById(bookingId, bookerId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<BookingDto> getBookingById(Long bookingId, Long userId) {
        log.info("Запрос на получение  бронирования с ID: {} от пользователя с ID: {}", bookingId, userId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.getBookingById(bookingId, userId));
    }

    public ResponseEntity<Collection<BookingDto>> getAllBookingByBookerId(Long bookerId, State state) {
        log.info("Запрос на получение всех бронирований пользователя с ID: {}", bookerId);
        validation.userIdValidation(bookerId);

        return ResponseEntity.ok().body(gatewayServiceClient.getAllBookingByBookerId(bookerId, state));
    }

    public ResponseEntity<Collection<BookingDto>> getAllBookingByOwnerId(Long ownerId, State state) {
        log.info("Запрос на получение всех бронирований владельца с ID: {}", ownerId);
        validation.userIdValidation(ownerId);

        return ResponseEntity.ok().body(gatewayServiceClient.getAllBookingByOwnerId(ownerId, state));
    }
}
