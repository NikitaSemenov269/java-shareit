package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;

public interface BookingRepository {

    void addBooking(Booking booking);

    Booking updateBooking(Booking updateBooking);

    void canceledBookingById(Long bookingId);

    void deleteBooking(Long bookingId);

    Booking updateAvailableStatusBooking(Long bookingId, BookingStatus bookingStatus);

    Booking getBookingById(Long bookingId);

    boolean checkingBookingDates(LocalDateTime startRent, LocalDateTime endRent);
}
