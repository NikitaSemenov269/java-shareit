package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.DTO.SimpleItemDto;
import ru.practicum.DTO.SimpleUserDto;
import ru.practicum.enums.BookingStatus;
import ru.practicum.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.booking.interfaces.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;

    private BookingRequestDto createBookingRequestDto() {
        return new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING
        );
    }

    private BookingDto createBookingDto() {
        return new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new SimpleItemDto(itemId, "Test Item"),
                new SimpleUserDto(userId)
        );
    }

    @Test
    void createBooking_ShouldReturnCreated() throws Exception {
        BookingRequestDto requestDto = createBookingRequestDto();
        BookingDto responseDto = createBookingDto();

        when(bookingService.createBooking(eq(userId), any(BookingRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        BookingRequestDto requestDto = createBookingRequestDto();

        when(bookingService.createBooking(eq(userId), any(BookingRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAvailableStatusBooking_ShouldReturnOk() throws Exception {
        BookingDto responseDto = createBookingDto();
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.updateAvailableStatusBooking(userId, bookingId, true))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void canceledBookingById_ShouldReturnNoContent() throws Exception {
        doNothing().when(bookingService).canceledBookingById(userId, bookingId);

        mockMvc.perform(patch("/bookings/cancel/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        BookingDto responseDto = createBookingDto();

        when(bookingService.getBookingById(userId, bookingId))
                .thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getAllBookingByBookerId_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(createBookingDto());

        when(bookingService.getAllBookingByBookerId(userId, State.ALL))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }

    @Test
    void getAllBookingByOwnerId_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(createBookingDto());

        when(bookingService.getAllBookingByOwnerId(userId, State.ALL))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }
}