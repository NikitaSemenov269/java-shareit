package ru.practicum.shareit.booking.interfacesBooking;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.enums.BookingStatus;

public interface BookingServiceInterface {

    Booking createBooking(Long bookerId, Booking booking);

    Booking updateBooking(Long bookingId, Long bookerId, Booking updateBooking);

    void canceledBookingById(Long bookerId, Long bookingId);

    void deleteBooking(Long bookerId, Long bookingId);

    Booking updateAvailableStatusBooking(Long idOwner, Long bookingId, BookingStatus bookingStatus);

    Booking getBookingById(Long bookingId);
}
