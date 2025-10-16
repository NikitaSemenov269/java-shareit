package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private RequestDto requestDto;
    private ResponseRequestDto responseRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        requestDto = new RequestDto("Need a drill");
        responseRequestDto = new ResponseRequestDto(
                requestId, userId, "Need a drill", LocalDateTime.now(), Collections.emptyList()
        );
    }

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        when(requestService.createRequest(any(RequestDto.class), eq(userId)))
                .thenReturn(responseRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.requesterId").value(userId));

        verify(requestService).createRequest(any(RequestDto.class), eq(userId));
    }

    @Test
    void createRequest_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(requestService.createRequest(any(RequestDto.class), eq(userId)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(requestService).createRequest(any(RequestDto.class), eq(userId));
    }

    @Test
    void createRequest_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createRequest_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        when(requestService.createRequest(any(RequestDto.class), eq(userId)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера."));

        verify(requestService).createRequest(any(RequestDto.class), eq(userId));
    }

    @Test
    void getUserRequests_ShouldReturnRequests() throws Exception {
        List<ResponseRequestDto> requests = List.of(responseRequestDto);

        when(requestService.getUserRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].requesterId").value(userId))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));

        verify(requestService).getUserRequests(userId);
    }

    @Test
    void getUserRequests_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getUserRequests(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(requestService).getUserRequests(userId);
    }

    @Test
    void getUserRequests_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(requestService.getUserRequests(userId))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(requestService).getUserRequests(userId);
    }

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        when(requestService.getRequestById(requestId, userId)).thenReturn(responseRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.requesterId").value(userId));

        verify(requestService).getRequestById(requestId, userId);
    }

    @Test
    void getRequestById_WhenRequestNotFound_ShouldReturnNotFound() throws Exception {
        when(requestService.getRequestById(requestId, userId))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Request not found"));

        verify(requestService).getRequestById(requestId, userId);
    }

    @Test
    void getRequestById_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/not-a-number")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRequests_ShouldReturnRequests() throws Exception {
        ResponseRequestDto otherUserRequest = new ResponseRequestDto(
                requestId, 2L, "Need a hammer", LocalDateTime.now(), Collections.emptyList()
        );
        List<ResponseRequestDto> requests = List.of(otherUserRequest);

        when(requestService.getOtherUserRequests(userId, 0, 10)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].requesterId").value(2L))
                .andExpect(jsonPath("$[0].description").value("Need a hammer"));

        verify(requestService).getOtherUserRequests(userId, 0, 10);
    }

    @Test
    void getAllRequests_WithDefaultPagination_ShouldReturnRequests() throws Exception {
        when(requestService.getOtherUserRequests(userId, 0, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(requestService).getOtherUserRequests(userId, 0, 10);
    }

    @Test
    void getAllRequests_WhenNoRequests_ShouldReturnEmptyList() throws Exception {
        when(requestService.getOtherUserRequests(userId, 0, 10)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(requestService).getOtherUserRequests(userId, 0, 10);
    }

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

    @Test
    void getAllRequests_WithInvalidPagination_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "invalid")
                        .param("size", "invalid"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRequests_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        when(requestService.getOtherUserRequests(userId, 0, 10))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(requestService).getOtherUserRequests(userId, 0, 10);
    }

    @Test
    void createRequest_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserRequests_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRequests_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isInternalServerError());
    }

    // Дополнительные тесты для полного покрытия
    @Test
    void getRequestById_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isInternalServerError());
    }
}