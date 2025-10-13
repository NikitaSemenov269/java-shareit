package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.BookingDto;
import ru.practicum.enums.State;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.interfaces.BookingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void getBookingById_Success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(true);

        when(bookingService.getBookingById(1L, 1L)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(true));

        verify(bookingService).getBookingById(1L, 1L);
    }

    @Test
    void getBookingById_WithoutUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingByBookerId_Success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        when(bookingService.getAllBookingByBookerId(1L, State.ALL))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingService).getAllBookingByBookerId(1L, State.ALL);
    }

    @Test
    void getAllBookingByBookerId_DefaultState() throws Exception {
        when(bookingService.getAllBookingByBookerId(1L, State.ALL))
                .thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingByBookerId(1L, State.ALL);
    }

    @Test
    void getAllBookingByOwnerId_Success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        when(bookingService.getAllBookingByOwnerId(1L, State.ALL))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingService).getAllBookingByOwnerId(1L, State.ALL);
    }

    @Test
    void createBooking_Success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        when(bookingService.createBooking(eq(1L), any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 1, \"start\": \"2024-01-01T10:00:00\", \"end\": \"2024-01-02T10:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingService).createBooking(eq(1L), any());
    }


    @Test
    void createBooking_WithoutUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_InvalidJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAvailableStatusBooking_Success() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);

        when(bookingService.updateAvailableStatusBooking(1L, 1L, true))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingService).updateAvailableStatusBooking(1L, 1L, true);
    }

    @Test
    void updateAvailableStatusBooking_WithoutApprovedParam_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canceledBookingById_Success() throws Exception {
        mockMvc.perform(patch("/bookings/cancel/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNoContent());

        verify(bookingService).canceledBookingById(1L, 1L);
    }

    @Test
    void canceledBookingById_WithoutUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/cancel/1"))
                .andExpect(status().isBadRequest());
    }
}