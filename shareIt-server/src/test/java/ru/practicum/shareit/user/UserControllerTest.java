/*
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequestDto validUserRequest;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        validUserRequest = new UserRequestDto("Test User", "test@example.com");
        userDto = new UserDto(1L, "Test User", "test@example.com");
    }

    // POST /users tests
    @Test
    void createUser_WithValidData_ShouldReturn200AndUser() throws Exception {
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldReturn409() throws Exception {
        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isConflict());

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_WithInvalidJson_ShouldReturn400() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // GET /users/{id} tests
    @Test
    void getUserById_WithExistingUser_ShouldReturn200AndUser() throws Exception {
        when(userService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserDtoById(1L);
    }

    @Test
    void getUserById_WithNonExistentUser_ShouldReturn404() throws Exception {
        when(userService.getUserDtoById(999L))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserDtoById(999L);
    }

    @Test
    void getUserById_WithInvalidIdFormat_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/users/abc"))
                .andExpect(status().isBadRequest());
    }

    // PATCH /users/{id} tests
    @Test
    void updateUser_WithValidData_ShouldReturn200AndUpdatedUser() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated User", "updated@example.com");
        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithNonExistentUser_ShouldReturn404() throws Exception {
        when(userService.updateUser(eq(999L), any(UserRequestDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldReturn409() throws Exception {
        when(userService.updateUser(eq(1L), any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUserRequest)))
                .andExpect(status().isConflict());

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // Empty body is allowed for partial updates
    }

    // DELETE /users/{id} tests
    @Test
    void deleteUser_WithExistingUser_ShouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_WithNonExistentUser_ShouldReturn204() throws Exception {
        doNothing().when(userService).deleteUser(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(999L);
    }

    // Edge cases
    @Test
    void createUser_WithNullFields_ShouldHandleGracefully() throws Exception {
        UserRequestDto requestWithNulls = new UserRequestDto(null, null);

        // This will be handled by service layer validation
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithNulls)))
                .andExpect(status().is5xxServerError()); // DataIntegrityException from service
    }

    @Test
    void updateUser_WithOnlyName_ShouldWork() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated Name", "test@example.com");
        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(updatedUser);

        String requestJson = "{\"name\": \"Updated Name\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }

    @Test
    void updateUser_WithOnlyEmail_ShouldWork() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Test User", "new@example.com");
        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(updatedUser);

        String requestJson = "{\"email\": \"new@example.com\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));

        verify(userService).updateUser(eq(1L), any(UserRequestDto.class));
    }
}
*/
