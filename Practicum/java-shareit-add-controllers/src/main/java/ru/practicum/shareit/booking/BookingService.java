package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.interfacesBooking.BookingRepositoryInterface;
import ru.practicum.shareit.booking.interfacesBooking.BookingServiceInterface;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfacesUser.UserRepositoryInterface;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService implements BookingServiceInterface {
    private final BookingRepositoryInterface bookingRepositoryInterface;
    private final UserRepositoryInterface userRepositoryInterface;
    private final BookingValidator bookingValidator;

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public Booking createBooking(Long bookerId, Booking booking) {
        booking.setId(counter.getAndIncrement());
        Long id = booking.getId();
        log.info("Попытка создания новой брони с ID: {}", id);
        bookingValidator.bookingValidationById(id);
        bookingValidator.bookerValidationById(bookerId);
        if (!userRepositoryInterface.existsByUserId(bookerId)) {
            throw new ValidationException("Пользователь с " + bookerId + " не существует");
        }
        booking.setBookerId(bookerId);
        bookingRepositoryInterface.addBooking(booking);
        log.info("Создан новый предмет с ID: {}", id);
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookerId, Booking updateBooking) {
        bookingValidator.bookingValidationById(updateBooking.getId());
        bookingValidator.bookerValidationById(bookerId);
        if (!userRepositoryInterface.existsByUserId(bookerId)) {
            throw new ValidationException("Пользователь с " + bookerId + " не существует");
        }
        bookingValidator.bookingValidationBelongsByIdOwner(bookerId, updateBooking.getId());
        Long updateBookingId = updateBooking.getId();
        log.info("Попытка обновления данных предмета с ID: {}", updateBookingId);
        Booking booking = bookingRepositoryInterface.updateBooking(updateBooking);
        log.info("Данные предмета с ID: {} успешно обновлены", updateBookingId);
        return booking;
    }
}

