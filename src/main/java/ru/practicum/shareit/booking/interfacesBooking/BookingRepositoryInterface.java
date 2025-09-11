package ru.practicum.shareit.booking.interfacesBooking;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.enums.BookingStatus;

public interface BookingRepositoryInterface {

    void addBooking(Booking booking);

    Booking updateBooking(Booking updateBooking);

    void deleteBooking(Long bookingId);

    Booking updateAvailableStatusBooking(Long bookingId, BookingStatus bookingStatus);
}
