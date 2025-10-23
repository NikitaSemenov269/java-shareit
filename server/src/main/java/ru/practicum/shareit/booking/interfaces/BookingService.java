package ru.practicum.shareit.booking.interfaces;


import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.State;

import java.util.Collection;


public interface BookingService {

    BookingDto createBooking(Long bookerId, BookingRequestDto bookingRequestDto);

    void canceledBookingById(Long bookerId, Long bookingId);

    BookingDto updateAvailableStatusBooking(Long idOwner, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    Collection<BookingDto> getAllBookingByBookerId(Long bookerId, State state);

    Collection<BookingDto> getAllBookingByOwnerId(Long ownerId, State state);
}

