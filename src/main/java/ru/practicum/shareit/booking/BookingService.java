package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.interfacesBooking.BookingRepositoryInterface;
import ru.practicum.shareit.booking.interfacesBooking.BookingServiceInterface;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.interfacesItem.ItemServiceInterface;

import java.util.concurrent.atomic.AtomicLong;

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
        bookingValidator.existsByBookingId(bookerId);
        bookingValidator.bookingDateValidation(booking.getStartRent(), booking.getEndRent());

        booking.setBookerId(bookerId);
        bookingRepositoryInterface.addBooking(booking);
        log.info("Создана новая заявка бронирования c ID: {} для предмета с ID: {}", id, booking.getItemId());
        return booking;
    }

    @Override
    public Booking updateBooking(Long bookerId, Booking updateBooking) {

        bookingValidator.bookingValidationById(updateBooking.getBookingId());
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.existsByBookingId(bookerId);
        bookingValidator.bookingValidationByIdItem(updateBooking.getItemId());
        bookingValidator.bookingValidationBelongsByIdBooker(bookerId, updateBooking.getBookingId());
        bookingValidator.bookingDateValidation(updateBooking.getStartRent(), updateBooking.getEndRent());

        Long updateBookingId = updateBooking.getBookingId();
        log.info("Попытка обновления данных предмета с ID: {}", updateBookingId);
        Booking booking = bookingRepositoryInterface.updateBooking(updateBooking);
        log.info("Данные предмета с ID: {} успешно обновлены", updateBookingId);
        return booking;
    }

    @Override
    public void deleteBooking(Long bookerId, Long bookingId) {
        log.info("Попытка удаления брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        bookingValidator.bookerValidationById(bookerId);
        bookingValidator.existsByBookingId(bookerId);
        bookingValidator.bookingValidationBelongsByIdBooker(bookerId, bookingId);

        bookingRepositoryInterface.deleteBooking(bookingId);
        log.info("Успешное удаление брони с ID: {}", bookingId);
    }
    /* Должно принимать значения и передавать их дальше в itemService для обновления статуса вещи и заявки.
       Добавить в bookingRepository метод получения BookingDto и создать DTO, что бы можно было получить idItem.
       Данную сущность может получать только автор заявки и владелец вещи? Может стоит создать
       метод на получение только на получени idItem по idBooking ?
     */

    @Override
    public Booking updateAvailableStatusBooking(Long ownerId, Long bookingId, BookingStatus bookingStatus) {
        log.info("Попытка обновления статуса брони с ID: {}", bookingId);

        bookingValidator.bookingValidationById(bookingId);
        //допущение: вместо id автора заявки используется id владельца вещи.
        bookingValidator.existsByBookingId(ownerId);
//        //допущение: вместо id автора заявки используется id вещи.
//        bookingValidator.bookingValidationByIdItem(bookingId);
//        itemServiceInterface.updateItemAvailable(ownerId, ,bookingStatus);

        return bookingRepositoryInterface.updateAvailableStatusBooking(bookingId, bookingStatus);
    }
}

