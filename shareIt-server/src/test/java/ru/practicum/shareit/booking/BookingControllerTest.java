package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private final Long userId = 1L;
    private final Long bookingId = 1L;

    @BeforeEach
    void setUp() {
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingDto = new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new SimpleItemDto(1L, "Test Item"),
                new SimpleUserDto(userId)
        );
    }

    @Test
    void createBooking_WithValidData_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())))
                .andExpect(jsonPath("$.status", is("WAITING")));

        verify(bookingService).createBooking(eq(userId), any());
    }

    @Test
    void createBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WithNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAvailableStatusBooking_ApproveBooking_ShouldReturnUpdatedBooking() throws Exception {
        BookingDto approvedBookingDto = new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                new SimpleItemDto(1L, "Test Item"),
                new SimpleUserDto(userId)
        );

        when(bookingService.updateAvailableStatusBooking(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(approvedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        verify(bookingService).updateAvailableStatusBooking(userId, bookingId, true);
    }

    @Test
    void getBookingById_WithValidData_ShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(userId, bookingId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingId.intValue())));

        verify(bookingService).getBookingById(userId, bookingId);
    }

    @Test
    void getAllBookingByBookerId_WithDefaultState_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = Arrays.asList(bookingDto);
        when(bookingService.getAllBookingByBookerId(userId, State.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService).getAllBookingByBookerId(userId, State.ALL);
    }

    @Test
    void getAllBookingByOwnerId_WithValidData_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = Arrays.asList(bookingDto);
        when(bookingService.getAllBookingByOwnerId(userId, State.ALL)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService).getAllBookingByOwnerId(userId, State.ALL);
    }

    @Test
    void canceledBookingById_WithValidData_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/bookings/cancel/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNoContent());
    }
}