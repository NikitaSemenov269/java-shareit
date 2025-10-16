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
        BookingRequestDto requestDto = new BookingRequestDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 2, 10, 0),
                BookingStatus.WAITING
        );

        Booking booking = bookingMapper.toBooking(requestDto);

        assertThat(booking).isNotNull();
        assertThat(booking.getStart()).isEqualTo(requestDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(requestDto.getEnd());
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void toDto_WithValidBooking_ShouldMapCorrectly() {
        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker Name");
        booker.setEmail("booker@example.com");

        User owner = new User();
        owner.setId(3L);
        owner.setName("Owner Name");
        owner.setEmail("owner@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2024, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2024, 1, 2, 10, 0));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(booking.getStart());
        assertThat(dto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getItem().getName()).isEqualTo("Test Item");
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void toDto_WithNullItem_ShouldReturnNull() {
        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(null);
        booking.setBooker(booker);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getItem()).isNull();
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void toDto_WithNullUser_ShouldReturnNull() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(null);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getBooker()).isNull();
        assertThat(dto.getItem().getId()).isEqualTo(1L);
    }
}