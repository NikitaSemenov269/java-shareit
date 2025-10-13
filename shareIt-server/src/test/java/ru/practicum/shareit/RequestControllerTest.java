package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.ResponseRequestDto;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.interfaces.RequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Test
    void createRequest_Success() throws Exception {
        ResponseRequestDto requestDto = new ResponseRequestDto();
        requestDto.setId(1L);
        requestDto.setDescriptionRequest("Need item");

        when(requestService.createRequest(any(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"description\": \"Need item\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descriptionRequest").value("Need item"));
    }

    @Test
    void getRequestById_Success() throws Exception {
        ResponseRequestDto requestDto = new ResponseRequestDto();
        requestDto.setId(1L);

        when(requestService.getRequestById(1L, 1L)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserRequests_Success() throws Exception {
        ResponseRequestDto requestDto = new ResponseRequestDto();
        requestDto.setId(1L);

        when(requestService.getUserRequests(1L)).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllRequests_Success() throws Exception {
        ResponseRequestDto requestDto = new ResponseRequestDto();
        requestDto.setId(1L);

        when(requestService.getOtherUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}