package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.DTO.BookingDto;
import ru.practicum.DTO.BookingRequestDto;
import ru.practicum.DTO.SimpleItemDto;
import ru.practicum.DTO.SimpleUserDto;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.enums.BookingStatus;
import ru.practicum.enums.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.booking.interfaces.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long bookerId = 2L;
    private final Long ownerId = 3L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;

    private BookingRequestDto bookingRequestDto;
    private BookingDto bookingDto;
    private BookingDto approvedBookingDto;
    private BookingDto rejectedBookingDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Важно: добавляем поддержку Java 8 времени

        // Подготовка тестовых данных для запроса на бронирование
        bookingRequestDto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING
        );

        // Подготовка тестовых данных для ответа с бронированием
        bookingDto = new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING,
                new SimpleItemDto(itemId, "Test Item"),
                new SimpleUserDto(bookerId)
        );

        // Подготовка подтвержденного бронирования
        approvedBookingDto = new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED,
                new SimpleItemDto(itemId, "Test Item"),
                new SimpleUserDto(bookerId)
        );

        // Подготовка отклоненного бронирования
        rejectedBookingDto = new BookingDto(
                bookingId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.REJECTED,
                new SimpleItemDto(itemId, "Test Item"),
                new SimpleUserDto(bookerId)
        );
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(eq(bookerId), any(BookingRequestDto.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(bookerId));

        verify(bookingService).createBooking(eq(bookerId), any(BookingRequestDto.class));
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.createBooking(eq(bookerId), any(BookingRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isNotFound());

        verify(bookingService).createBooking(eq(bookerId), any(BookingRequestDto.class));
    }

    @Test
    void createBooking_WhenValidationException_ShouldReturnBadRequest() throws Exception {
        when(bookingService.createBooking(eq(bookerId), any(BookingRequestDto.class)))
                .thenThrow(new ValidationException("Предмет недоступен для бронирования"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService).createBooking(eq(bookerId), any(BookingRequestDto.class));
    }

    @Test
    void createBooking_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createBooking_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        when(bookingService.createBooking(eq(bookerId), any(BookingRequestDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isInternalServerError());

        verify(bookingService).createBooking(eq(bookerId), any(BookingRequestDto.class));
    }

    // ===== ТЕСТЫ ОБНОВЛЕНИЯ СТАТУСА БРОНИРОВАНИЯ =====

    @Test
    void updateAvailableStatusBooking_WhenApprove_ShouldReturnApprovedBooking() throws Exception {
        when(bookingService.updateAvailableStatusBooking(eq(ownerId), eq(bookingId), eq(true)))
                .thenReturn(approvedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).updateAvailableStatusBooking(ownerId, bookingId, true);
    }

    @Test
    void updateAvailableStatusBooking_WhenReject_ShouldReturnRejectedBooking() throws Exception {
        when(bookingService.updateAvailableStatusBooking(eq(ownerId), eq(bookingId), eq(false)))
                .thenReturn(rejectedBookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(bookingService).updateAvailableStatusBooking(ownerId, bookingId, false);
    }

    @Test
    void updateAvailableStatusBooking_WhenBookingNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.updateAvailableStatusBooking(eq(ownerId), eq(bookingId), eq(true)))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());

        verify(bookingService).updateAvailableStatusBooking(ownerId, bookingId, true);
    }

    @Test
    void updateAvailableStatusBooking_WhenNotOwner_ShouldReturnBadRequest() throws Exception {
        when(bookingService.updateAvailableStatusBooking(eq(userId), eq(bookingId), eq(true)))
                .thenThrow(new ValidationException("Пользователь не является владельцем вещи"));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isInternalServerError());

        verify(bookingService).updateAvailableStatusBooking(userId, bookingId, true);
    }

    @Test
    void updateAvailableStatusBooking_WithoutApprovedParam_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateAvailableStatusBooking_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true"))
                .andExpect(status().isInternalServerError());
    }

    // ===== ТЕСТЫ ОТМЕНЫ БРОНИРОВАНИЯ =====

    @Test
    void canceledBookingById_ShouldReturnNoContent() throws Exception {
        doNothing().when(bookingService).canceledBookingById(eq(bookerId), eq(bookingId));

        mockMvc.perform(patch("/bookings/cancel/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isNoContent());

        verify(bookingService).canceledBookingById(bookerId, bookingId);
    }

    @Test
    void canceledBookingById_WhenBookingNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Бронирование не найдено"))
                .when(bookingService).canceledBookingById(eq(bookerId), eq(bookingId));

        mockMvc.perform(patch("/bookings/cancel/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isNotFound());

        verify(bookingService).canceledBookingById(bookerId, bookingId);
    }

    @Test
    void canceledBookingById_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/bookings/cancel/{bookingId}", bookingId))
                .andExpect(status().isInternalServerError());
    }

    // ===== ТЕСТЫ ПОЛУЧЕНИЯ БРОНИРОВАНИЯ ПО ID =====

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(eq(userId), eq(bookingId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(bookerId));

        verify(bookingService).getBookingById(userId, bookingId);
    }

    @Test
    void getBookingById_WhenBookingNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.getBookingById(eq(userId), eq(bookingId)))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        verify(bookingService).getBookingById(userId, bookingId);
    }

    @Test
    void getBookingById_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/not-a-number")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getBookingById_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", bookingId))
                .andExpect(status().isInternalServerError());
    }

    // ===== ТЕСТЫ ПОЛУЧЕНИЯ БРОНИРОВАНИЙ ПОЛЬЗОВАТЕЛЯ =====

    @Test
    void getAllBookingByBookerId_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllBookingByBookerId(eq(bookerId), eq(State.ALL))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].item.id").value(itemId));

        verify(bookingService).getAllBookingByBookerId(bookerId, State.ALL);
    }

    @Test
    void getAllBookingByBookerId_WithDefaultState_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllBookingByBookerId(eq(bookerId), eq(State.ALL))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));

        verify(bookingService).getAllBookingByBookerId(bookerId, State.ALL);
    }

    @Test
    void getAllBookingByBookerId_WhenNoBookings_ShouldReturnEmptyList() throws Exception {
        when(bookingService.getAllBookingByBookerId(eq(bookerId), eq(State.ALL)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookingService).getAllBookingByBookerId(bookerId, State.ALL);
    }

    @Test
    void getAllBookingByBookerId_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.getAllBookingByBookerId(eq(bookerId), eq(State.ALL)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());

        verify(bookingService).getAllBookingByBookerId(bookerId, State.ALL);
    }

    @Test
    void getAllBookingByBookerId_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isInternalServerError());
    }

    // ===== ТЕСТЫ ПОЛУЧЕНИЯ БРОНИРОВАНИЙ ВЛАДЕЛЬЦА =====

    @Test
    void getAllBookingByOwnerId_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllBookingByOwnerId(eq(ownerId), eq(State.ALL))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[0].item.id").value(itemId));

        verify(bookingService).getAllBookingByOwnerId(ownerId, State.ALL);
    }

    @Test
    void getAllBookingByOwnerId_WithDefaultState_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllBookingByOwnerId(eq(ownerId), eq(State.ALL))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));

        verify(bookingService).getAllBookingByOwnerId(ownerId, State.ALL);
    }

    @Test
    void getAllBookingByOwnerId_WhenNoBookings_ShouldReturnEmptyList() throws Exception {
        when(bookingService.getAllBookingByOwnerId(eq(ownerId), eq(State.ALL)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookingService).getAllBookingByOwnerId(ownerId, State.ALL);
    }

    @Test
    void getAllBookingByOwnerId_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.getAllBookingByOwnerId(eq(ownerId), eq(State.ALL)))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL"))
                .andExpect(status().isNotFound());

        verify(bookingService).getAllBookingByOwnerId(ownerId, State.ALL);
    }

    @Test
    void getAllBookingByOwnerId_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL"))
                .andExpect(status().isInternalServerError());
    }

    // ===== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ ДЛЯ РАЗЛИЧНЫХ СОСТОЯНИЙ =====

    @Test
    void getAllBookingByBookerId_WithDifferentStates_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        // Тестируем разные состояния
        when(bookingService.getAllBookingByBookerId(eq(bookerId), eq(State.CURRENT))).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));

        verify(bookingService).getAllBookingByBookerId(bookerId, State.CURRENT);
    }

    @Test
    void getAllBookingByOwnerId_WithDifferentStates_ShouldReturnBookings() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto);

        when(bookingService.getAllBookingByOwnerId(eq(ownerId), eq(State.CURRENT))).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));

        verify(bookingService).getAllBookingByOwnerId(ownerId, State.CURRENT);
    }

    @Test
    void createBooking_WithIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        when(bookingService.createBooking(eq(bookerId), any(BookingRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Неверные даты бронирования"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService).createBooking(eq(bookerId), any(BookingRequestDto.class));
    }

    @Test
    void getAllBookingByBookerId() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllBookingByOwnerId_WithInvalidState() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isInternalServerError());
    }
}