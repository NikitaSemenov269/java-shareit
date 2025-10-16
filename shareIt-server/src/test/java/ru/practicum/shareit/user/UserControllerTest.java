package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.shareit.user.interfaces.UserService;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserRequestDto userRequestDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Test User", "test@example.com");
        userRequestDto = new UserRequestDto("Test User", "test@example.com");
    }

    @Test
    void createUser_WithValidData_ShouldReturnUser() throws Exception {
        when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldReturnInternalServerError() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new EmailAlreadyExistsException("Пользователь с таким email уже существует."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("Пользователь с таким email уже существует")));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() throws Exception {
        when(userService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).getUserDtoById(1L);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(userService.getUserDtoById(999L))
                .thenThrow(new NotFoundException("Пользователь с ID: 999 не найден"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь с ID: 999 не найден")));

        verify(userService, times(1)).getUserDtoById(999L);
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated User", "updated@example.com");
        when(userService.updateUser(eq(1L), any())).thenReturn(updatedUser);

        UserRequestDto updateRequest = new UserRequestDto("Updated User", "updated@example.com");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any());
    }

    @Test
    void updateUser_WithPartialData_ShouldReturnUpdatedUser() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated User", "test@example.com");
        when(userService.updateUser(eq(1L), any())).thenReturn(updatedUser);

        String partialUpdateJson = "{\"name\": \"Updated User\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated User")));

        verify(userService, times(1)).updateUser(eq(1L), any());
    }

    @Test
    void updateUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        when(userService.updateUser(eq(999L), any()))
                .thenThrow(new NotFoundException("Пользователь с 999 не существует"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь с 999 не существует")));

        verify(userService, times(1)).updateUser(eq(999L), any());
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldReturnInternalServerError() throws Exception {
        when(userService.updateUser(eq(1L), any()))
                .thenThrow(new EmailAlreadyExistsException("Email уже занят другим пользователем."));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", containsString("Email уже занят другим пользователем")));

        verify(userService, times(1)).updateUser(eq(1L), any());
    }

    @Test
    void deleteUser_WithExistingId_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).deleteUser(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь не найден")));

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    void createUser_WithMalformedJSON_ShouldReturnBadRequest() throws Exception {
        String malformedJson = "{\"name\": \"Test User\", \"email\": \"test@example.com\"";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_WithMalformedJSON_ShouldReturnBadRequest() throws Exception {
        String malformedJson = "{\"name\": \"Updated User\"";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/users/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_WithInvalidIdFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/users/invalid"))
                .andExpect(status().isBadRequest());
    }
}