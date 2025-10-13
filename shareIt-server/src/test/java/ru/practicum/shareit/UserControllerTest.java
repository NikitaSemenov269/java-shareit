package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.DTO.UserDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.interfaces.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getUserDtoById_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");

        when(userService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void createUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@mail.ru");

        when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\": \"John\", \"email\": \"john@mail.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Updated Name");

        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}