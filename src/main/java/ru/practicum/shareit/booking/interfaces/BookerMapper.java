package ru.practicum.shareit.booking.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingRequestDto;

@Mapper(componentModel = "spring")
public interface BookerMapper {

    @Mapping(target = "itemId", ignore = true)
    Booking toBooking(BookingRequestDto bookingRequestDto);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingDto bookerToBookerDto(Booking booking);
}
