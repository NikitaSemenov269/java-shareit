package ru.practicum.shareit.booking.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.DTO.BookingDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.DTO.BookingRequestDto;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingRequestDto bookingRequestDto);

    @Mapping(target = "status", expression = "java(booking.getStatus().isStatus())")
    BookingDto toDto(Booking booking);
}
