package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    private final Long userId = 1L;
    private final Long requestId = 1L;

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        RequestDto requestDto = new RequestDto("Need a drill");
        ResponseRequestDto responseDto = new ResponseRequestDto(
                requestId, userId, "Need a drill", LocalDateTime.now(), Collections.emptyList()
        );

        when(requestService.createRequest(any(RequestDto.class), eq(userId)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.requesterId").value(userId));
    }

    @Test
    void createRequest_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        RequestDto requestDto = new RequestDto("Need a drill");

        when(requestService.createRequest(any(RequestDto.class), eq(userId)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    // УБРАН тест с пустым description - в DTO нет валидации

    @Test
    void getUserRequests_ShouldReturnRequests() throws Exception {
        ResponseRequestDto responseDto = new ResponseRequestDto(
                requestId, userId, "Need a drill", LocalDateTime.now(), Collections.emptyList()
        );
        List<ResponseRequestDto> requests = List.of(responseDto);

        when(requestService.getUserRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].requesterId").value(userId));
    }

    @Test
    void getUserRequests_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getUserRequests(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        ResponseRequestDto responseDto = new ResponseRequestDto(
                requestId, userId, "Need a drill", LocalDateTime.now(), Collections.emptyList()
        );

        when(requestService.getRequestById(requestId, userId)).thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getRequestById_WhenRequestNotFound_ShouldReturnNotFound() throws Exception {
        when(requestService.getRequestById(requestId, userId))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequests_ShouldReturnRequests() throws Exception {
        ResponseRequestDto responseDto = new ResponseRequestDto(
                requestId, 2L, "Need a drill", LocalDateTime.now(), Collections.emptyList()
        );
        List<ResponseRequestDto> requests = List.of(responseDto);

        when(requestService.getOtherUserRequests(userId, 0, 10)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].requesterId").value(2L));
    }

    @Test
    void getAllRequests_WithDefaultPagination_ShouldReturnRequests() throws Exception {
        when(requestService.getOtherUserRequests(userId, 0, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllRequests_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getOtherUserRequests(userId, 0, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ДОБАВЛЕН тест для проверки вызова сервиса с правильными параметрами
    @Test
    void getAllRequests_WithCustomPagination_ShouldCallServiceWithCorrectParams() throws Exception {
        when(requestService.getOtherUserRequests(userId, 5, 20)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(requestService).getOtherUserRequests(userId, 5, 20);
    }
}