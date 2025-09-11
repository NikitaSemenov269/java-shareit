package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.interfacesBooking.BookingRepositoryInterface;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BookingRepository implements BookingRepositoryInterface {

    private Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public void addBooking(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    @Override
    public Booking updateBooking(Booking updateBooking) {
        Booking booking = bookings.get(updateBooking.getBookingId());
        if (updateBooking.getStartRent() != null) {
            booking.setStartRent(updateBooking.getStartRent());
        }
        if (updateBooking.getEndRent() != null) {
            booking.setEndRent(updateBooking.getEndRent());
        }
        return booking;
    }

    // Требует доработки !!!
    @Override
    public Booking updateAvailableStatusBooking(Long bookingId, BookingStatus bookingStatus) {
        Booking booking = bookings.get(bookingId);
        booking.setAvailableItem(bookingStatus);
        return booking;
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookings.remove(bookingId);
    }

    //служебные методы
    protected Booking getBookingById(Long bookingId) {
        return bookings.get(bookingId);
    }

    protected boolean checkingBookingDates(LocalDate startRent, LocalDate endRent) {
        return bookings.values().stream()
                .noneMatch(b -> startRent.isBefore(b.getEndRent()) &&
                        endRent.isAfter(b.getStartRent()));
    }
}
