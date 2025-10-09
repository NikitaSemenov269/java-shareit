package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.enums.State;

import java.util.Collection;


public interface BookingRepositoryCustom {
    Collection<BookingDto> findBookingsByBookerAndState(Long bookerId, State state);

    Collection<BookingDto> findBookingsByOwnerAndState(Long ownerId, State state);

}

