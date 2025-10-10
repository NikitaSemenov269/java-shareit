package ru.practicum.shareit.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;

@RestController
@RequestMapping(path = "/users")
public class UserController {

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
