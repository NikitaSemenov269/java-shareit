package ru.practicum.shareit.booking.interfacesBooking;

import ru.practicum.shareit.booking.Booking;

public interface BookingRepositoryInterface {

    void addBooking(Booking booking);

    Booking updateBooking(Booking updateBooking);
}
