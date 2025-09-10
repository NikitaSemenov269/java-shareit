package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.interfacesBooking.BookingRepositoryInterface;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookingRepository implements BookingRepositoryInterface {

    private Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public void addBooking(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public Booking updateBooking(Booking updateBooking) {
        Booking booking = bookings.get(updateBooking.getItemId());
        booking.setStartRent(updateBooking.getStartRent());
        booking.setEndRent(updateBooking.getEndRent());
        return booking;
    }

    //служебный метод
    protected Booking getBookingById(Long bookingId) {
        return bookings.get(bookingId);
    }
}
