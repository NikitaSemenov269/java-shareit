package ru.practicum.shareit.user;

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
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.GlobalExceptionHandler;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDto userDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        userDto = new UserDto(1L, "Test User", "test@example.com");
        userRequestDto = new UserRequestDto("Test User", "test@example.com");
    }

    @Test
    void createUser_WithValidData() throws Exception {
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_WithDuplicateEmail() throws Exception {
        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Пользователь с таким email уже существует."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера."));

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void getUserById_WithExistingId() throws Exception {
        when(userService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserDtoById(1L);
    }

    @Test
    void getUserById_WithNonExistingId() throws Exception {
        when(userService.getUserDtoById(999L))
                .thenThrow(new NotFoundException("Пользователь с ID: 999 не найден"));
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с ID: 999 не найден"));

        verify(userService).getUserDtoById(999L);
    }

    @Test
    void updateUser_WithValidData() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated User", "updated@example.com");
        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(updatedUser);

        UserRequestDto updateRequest = new UserRequestDto("Updated User", "updated@example.com");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithPartialData() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated User", "test@example.com");
        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(updatedUser);

        String partialUpdateJson = "{\"name\": \"Updated User\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithNonExistingId() throws Exception {
        when(userService.updateUser(eq(999L), any(UserRequestDto.class)))
                .thenThrow(new NotFoundException("Пользователь с 999 не существует"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с 999 не существует"));

        verify(userService).updateUser(eq(999L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithDuplicateEmail() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email уже занят другим пользователем."));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера."));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void deleteUser_WithExistingId() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_WithNonExistingId() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).deleteUser(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));

        verify(userService).deleteUser(999L);
    }

    // Тесты с некорректными ID (проверяем Spring conversion)
    @Test
    void getUserById_WithInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/users/not-a-number"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteUser_WithInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/users/not-a-number"))
                .andExpect(status().isInternalServerError());
    }

    // Тесты с пустым телом запроса
    @Test
    void createUser_WithEmptyBody() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateUser_WithEmptyBody() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    // Дополнительные тесты для покрытия edge cases
    @Test
    void createUser_WithServiceException() throws Exception {
        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера."));

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithServiceException() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDto.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера."));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }
}