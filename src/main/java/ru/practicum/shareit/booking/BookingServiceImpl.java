package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.interfaces.BookingRepository;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.interfaces.ItemService;

import java.util.concurrent.atomic.AtomicLong;

import static ru.practicum.shareit.enums.BookingStatus.CANCELED;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidator bookingValidator;
    private final ItemService itemService;

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
        bookingRepository.addBooking(booking);
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
        Booking booking = bookingRepository.updateBooking(updateBooking);
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

        bookingRepository.canceledBookingById(bookingId);
        log.info("Успешное отмена брони с ID: {}", bookingId);
    }

    @Override
    public void deleteBooking(Long bookerId, Long bookingId) {
        log.info("Попытка удаления брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.existsByBookerId(bookerId);
        bookingValidator.bookingValidationBelongsByIdBooker(bookerId, bookingId);

        bookingRepository.deleteBooking(bookingId);
        log.info("Успешное удаление брони с ID: {}", bookingId);
    }

    @Override
    public Booking updateAvailableStatusBooking(Long ownerId, Long bookingId, BookingStatus bookingStatus) {
        log.info("Попытка обновления статуса брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        //допущение: вместо id автора заявки используется id владельца вещи.
        bookingValidator.existsByBookerId(ownerId);

        Long itemId = bookingRepository.getBookingById(bookingId).getItemId();
        //только владелец может изменять статус брони
        bookingValidator.bookingValidationOfTheItemOwner(ownerId, itemId);

        if (!CANCELED.equals(bookingStatus)) {
            itemService.updateItemAvailable(ownerId, itemId, bookingStatus);
            return bookingRepository.updateAvailableStatusBooking(bookingId, bookingStatus);
        } else {
            throw new ValidationException("Закрытие брони доступно только инициатору бронирования.");
        }
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        log.info("Попытка получения брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);

        return bookingRepository.getBookingById(bookingId);
    }
}