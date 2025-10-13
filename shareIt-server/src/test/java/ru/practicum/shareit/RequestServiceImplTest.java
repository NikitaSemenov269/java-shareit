package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.DTO.RequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestServiceImpl;
import ru.practicum.shareit.request.interfaces.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void createRequest_Success() {
        // Given
        Long userId = 1L;
        RequestDto requestDto = new RequestDto();
        requestDto.setDescriptionRequest("Need item");

        User user = new User(userId, "John", "john@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(Request.class))).thenReturn(new Request());

        // When
        var result = requestService.createRequest(requestDto, userId);

        // Then
        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_UserNotFound_ThrowsException() {
        // Given
        Long userId = 999L;
        RequestDto requestDto = new RequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            requestService.createRequest(requestDto, userId);
        });
    }

    @Test
    void getRequestById_Success() {
        // Given
        Long requestId = 1L;
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(new Request()));

        // When
        var result = requestService.getRequestById(requestId, userId);

        // Then
        assertNotNull(result);
        verify(requestRepository).findById(requestId);
    }

    @Test
    void getRequestById_RequestNotFound_ThrowsException() {
        // Given
        Long requestId = 999L;
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            requestService.getRequestById(requestId, userId);
        });
    }

    @Test
    void getRequestById_UserNotFound_ThrowsException() {
        // Given
        Long requestId = 1L;
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            requestService.getRequestById(requestId, userId);
        });
    }

    @Test
    void getUserRequests_Success() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of(new Request()));

        // When
        var result = requestService.getUserRequests(userId);

        // Then
        assertFalse(result.isEmpty());
        verify(requestRepository).findByRequesterId(userId);
    }

    @Test
    void getUserRequests_UserNotFound_ThrowsException() {
        // Given
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            requestService.getUserRequests(userId);
        });
    }

    @Test
    void getOtherUserRequests_Success() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllRequestsExceptUser(userId)).thenReturn(List.of(new Request()));

        // When
        var result = requestService.getOtherUserRequests(userId, 0, 10);

        // Then
        assertFalse(result.isEmpty());
        verify(requestRepository).findAllRequestsExceptUser(userId);
    }

    @Test
    void getOtherUserRequests_UserNotFound_ThrowsException() {
        // Given
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            requestService.getOtherUserRequests(userId, 0, 10);
        });
    }

    @Test
    void getUserRequests_EmptyList() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of());

        // When
        var result = requestService.getUserRequests(userId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getOtherUserRequests_EmptyList() {
        // Given
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllRequestsExceptUser(userId)).thenReturn(List.of());

        // When
        var result = requestService.getOtherUserRequests(userId, 0, 10);

        // Then
        assertTrue(result.isEmpty());
    }
}