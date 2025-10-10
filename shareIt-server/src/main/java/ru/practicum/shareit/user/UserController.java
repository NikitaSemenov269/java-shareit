package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.shareit.user.interfaces.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok().body(userService.createUser(userRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDtoById(
            @PathVariable @Min(1) Long id) {
        return ResponseEntity.ok().body(userService.getUserDtoById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable @Min(1) Long id,
            @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok().body(userService.updateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Min(1) Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
