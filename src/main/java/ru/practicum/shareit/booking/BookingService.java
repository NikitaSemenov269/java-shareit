package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.interfacesBooking.BookingRepositoryInterface;
import ru.practicum.shareit.booking.interfacesBooking.BookingServiceInterface;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfacesItem.ItemServiceInterface;

import java.util.concurrent.atomic.AtomicLong;

import static ru.practicum.shareit.enums.BookingStatus.CANCELED;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService implements BookingServiceInterface {

    private final BookingRepositoryInterface bookingRepositoryInterface;
    private final BookingValidator bookingValidator;
    private final ItemServiceInterface itemServiceInterface;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Booking createBooking(Long bookerId, Booking booking) {
        booking.setBookingId(counter.getAndIncrement());
        Long id = booking.getBookingId();
        log.info("Попытка создания новой брони с ID: {} для предмета с ID: {}", id, booking.getItemId());

        bookingValidator.bookingValidationById(id);
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.bookingValidationByIdItem(booking.getItemId());
        bookingValidator.existsByBookerId(bookerId);
        bookingValidator.bookingDateValidation(booking.getStartRent(), booking.getEndRent());

        booking.setBookerId(bookerId);
        bookingRepositoryInterface.addBooking(booking);
        log.info("Создана новая заявка бронирования c ID: {} для предмета с ID: {}", id, booking.getItemId());
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookingId, Long bookerId, Booking updateBooking) {

        bookingValidator.bookingValidationById(bookingId);
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.existsByBookerId(bookerId);
        bookingValidator.bookingValidationByIdItem(updateBooking.getItemId());
        bookingValidator.bookingValidationBelongsByIdBooker(bookerId, bookingId);
        bookingValidator.bookingDateValidation(updateBooking.getStartRent(), updateBooking.getEndRent());

        log.info("Попытка обновления данных предмета с ID: {}", bookingId);
        Booking booking = bookingRepositoryInterface.updateBooking(updateBooking);
        log.info("Данные предмета с ID: {} успешно обновлены", bookingId);
        return booking;
    }

    @Override
    public void canceledBookingById(Long bookerId, Long bookingId) {
        log.info("Попытка отмены брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.existsByBookerId(bookerId);
        bookingValidator.bookingValidationBelongsByIdBooker(bookerId, bookingId);

        bookingRepositoryInterface.canceledBookingById(bookingId);
        log.info("Успешное отмена брони с ID: {}", bookingId);
    }

    @Override
    public void deleteBooking(Long bookerId, Long bookingId) {
        log.info("Попытка удаления брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.existsByBookerId(bookerId);
        bookingValidator.bookingValidationBelongsByIdBooker(bookerId, bookingId);

        bookingRepositoryInterface.deleteBooking(bookingId);
        log.info("Успешное удаление брони с ID: {}", bookingId);
    }

    @Override
    public Booking updateAvailableStatusBooking(Long ownerId, Long bookingId, BookingStatus bookingStatus) {
        log.info("Попытка обновления статуса брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        //допущение: вместо id автора заявки используется id владельца вещи.
        bookingValidator.existsByBookerId(ownerId);

        Long itemId = bookingRepositoryInterface.getBookingById(bookingId).getItemId();
        //только владелец может изменять статус брони
        bookingValidator.bookingValidationOfTheItemOwner(ownerId, itemId);

        if (!CANCELED.equals(bookingStatus)) {
            itemServiceInterface.updateItemAvailable(ownerId, itemId, bookingStatus);
            return bookingRepositoryInterface.updateAvailableStatusBooking(bookingId, bookingStatus);
        } else {
            throw new ValidationException("Закрытие брони доступно только инициатору бронирования.");
        }
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        log.info("Попытка получения брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);

        return bookingRepositoryInterface.getBookingById(bookingId);
    }
}

