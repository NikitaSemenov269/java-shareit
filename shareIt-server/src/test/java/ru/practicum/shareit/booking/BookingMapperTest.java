package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toBooking_WithValidRequestDto_ShouldMapCorrectly() {
        // Given
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        requestDto.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));

        // When
        Booking booking = bookingMapper.toBooking(requestDto);

        // Then
        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isEqualTo(requestDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(requestDto.getEnd());
    }

    @Test
    void toDto_WithValidBooking_ShouldMapCorrectly() {
        // Given
        User booker = new User();
        booker.setId(2L);

        User owner = new User();
        owner.setId(3L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        // When
        BookingDto dto = bookingMapper.toDto(booking);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
    }
}