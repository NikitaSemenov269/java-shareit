package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.RequestDto;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private RequestDto requestDto;
    private ResponseRequestDto responseRequestDto;
    private final Long userId = 1L;
    private final Long requestId = 1L;

    @BeforeEach
    void setUp() {
        requestDto = new RequestDto();
        requestDto.setDescription("Need item for testing");

        responseRequestDto = new ResponseRequestDto();
        responseRequestDto.setId(requestId);
        responseRequestDto.setDescription("Need item for testing");
        responseRequestDto.setRequesterId(userId);
        responseRequestDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_WithValidData_ShouldReturnCreatedRequest() throws Exception {
        // Given
        when(requestService.createRequest(any(), anyLong())).thenReturn(responseRequestDto);

        // When & Then
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId.intValue())))
                .andExpect(jsonPath("$.description", is("Need item for testing")))
                .andExpect(jsonPath("$.requesterId", is(userId.intValue())));

        // Verify
        verify(requestService).createRequest(any(), eq(userId));
    }

    @Test
    void createRequest_WithoutUserIdHeader_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_ShouldReturnUserRequests() throws Exception {
        // Given
        Collection<ResponseRequestDto> requests = Arrays.asList(responseRequestDto);
        when(requestService.getUserRequests(userId)).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestId.intValue())))
                .andExpect(jsonPath("$[0].requesterId", is(userId.intValue())));

        // Verify
        verify(requestService).getUserRequests(userId);
    }

    @Test
    void getRequestById_WithValidData_ShouldReturnRequest() throws Exception {
        // Given
        when(requestService.getRequestById(requestId, userId)).thenReturn(responseRequestDto);

        // When & Then
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId.intValue())))
                .andExpect(jsonPath("$.description", is("Need item for testing")))
                .andExpect(jsonPath("$.requesterId", is(userId.intValue())));

        // Verify
        verify(requestService).getRequestById(requestId, userId);
    }

    @Test
    void getAllRequests_WithValidData_ShouldReturnRequests() throws Exception {
        // Given
        Collection<ResponseRequestDto> requests = Arrays.asList(responseRequestDto);
        when(requestService.getOtherUserRequests(eq(userId), anyInt(), anyInt())).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestId.intValue())));

        // Verify
        verify(requestService).getOtherUserRequests(userId, 0, 10);
    }

    @Test
    void getAllRequests_WithDefaultPagination_ShouldUseDefaults() throws Exception {
        // Given
        Collection<ResponseRequestDto> requests = Arrays.asList(responseRequestDto);
        when(requestService.getOtherUserRequests(eq(userId), anyInt(), anyInt())).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Verify - должны использоваться значения по умолчанию
        verify(requestService).getOtherUserRequests(userId, 0, 10);
    }

    @Test
    void getAllRequests_WithCustomPagination_ShouldUseCustomValues() throws Exception {
        // Given
        Collection<ResponseRequestDto> requests = Arrays.asList(responseRequestDto);
        when(requestService.getOtherUserRequests(eq(userId), anyInt(), anyInt())).thenReturn(requests);

        // When & Then
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk());

        // Verify - должны использоваться кастомные значения
        verify(requestService).getOtherUserRequests(userId, 5, 20);
    }
}