package ru.practicum.shareit.booking.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.SimpleItemDto;
import ru.practicum.DTO.SimpleUserDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingRequestDto bookingRequestDto);

    @Mapping(target = "item", expression = "java(mapItemToSimpleItemDto(booking.getItem()))")
    @Mapping(target = "booker", expression = "java(mapUserToSimpleUserDto(booking.getBooker()))")
    BookingDto toDto(Booking booking);

    default SimpleItemDto mapItemToSimpleItemDto(Item item) {
        if (item == null) return null;
        return new SimpleItemDto(item.getId(), item.getName());
    }

    default SimpleUserDto mapUserToSimpleUserDto(User user) {
        if (user == null) return null;
        return new SimpleUserDto(user.getId());
    }
}
