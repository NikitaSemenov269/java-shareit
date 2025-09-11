package ru.practicum.shareit.booking.interfacesBooking;

import ru.practicum.shareit.booking.Booking;

public interface BookingServiceInterface {

    Booking createBooking(Long bookerId, Booking booking);

    Booking updateBooking(Long bookerId, Booking updateBooking);

}
