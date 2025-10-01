package ru.practicum.shareit.booking.interfaces;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingRequestDto;

@Mapper(componentModel = "spring")
public interface BookerMapper {

    Booking bookingRequestDtoToBooking(BookingRequestDto bookingRequestDto);

    BookingDto bookerMapperToBookerMapperDto(Booking booking);
}
