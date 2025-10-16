package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final Long userId = 1L;
    private final String userName = "Test User";
    private final String userEmail = "test@example.com";

    @Test
    void createUser_ShouldReturnUserDto() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(userName, userEmail);
        UserDto responseDto = new UserDto(userId, userName, userEmail);

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        UserRequestDto invalidRequestDto = new UserRequestDto("", "invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_ShouldHandleEmailAlreadyExistsException() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(userName, userEmail);

        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());

        verify(userService).createUser(any(UserRequestDto.class));
    }

    @Test
    void getUserDtoById_ShouldReturnUserDto() throws Exception {
        UserDto responseDto = new UserDto(userId, userName, userEmail);

        when(userService.getUserDtoById(userId)).thenReturn(responseDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(userName))
                .andExpect(jsonPath("$.email").value(userEmail));

        verify(userService).getUserDtoById(userId);
    }

    @Test
    void getUserDtoById_ShouldHandleNotFoundException() throws Exception {
        when(userService.getUserDtoById(userId))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).getUserDtoById(userId);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", "updated@example.com");
        UserDto responseDto = new UserDto(userId, "Updated Name", "updated@example.com");

        when(userService.updateUser(eq(userId), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService).updateUser(eq(userId), any(UserRequestDto.class));
    }

    @Test
    void updateUser_ShouldHandleNotFoundException() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", "updated@example.com");

        when(userService.updateUser(eq(userId), any(UserRequestDto.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(userId), any(UserRequestDto.class));
    }

    @Test
    void updateUser_ShouldHandleEmailAlreadyExistsException() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated Name", "taken@example.com");

        when(userService.updateUser(eq(userId), any(UserRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException("Email taken"));

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());

        verify(userService).updateUser(eq(userId), any(UserRequestDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_ShouldHandleNotFoundException() throws Exception {
        doThrow(new NotFoundException("User not found")).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(userId);
    }

    @Test
    void createUser_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_WithPartialData_ShouldWork() throws Exception {
        UserRequestDto requestDto = new UserRequestDto(null, "updated@example.com");
        UserDto responseDto = new UserDto(userId, userName, "updated@example.com");

        when(userService.updateUser(eq(userId), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }
}
