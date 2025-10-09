package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.interfaces.BookingRepositoryCustom;
import ru.practicum.shareit.enums.State;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

    private final


    @Override
    public Collection<BookingDto> findBookingsByBookerAndState(Long bookerId, State state) {
        return List.of();
    }

    @Override
    public Collection<BookingDto> findBookingsByOwnerAndState(Long ownerId, State state) {
        return List.of();
    }
}
