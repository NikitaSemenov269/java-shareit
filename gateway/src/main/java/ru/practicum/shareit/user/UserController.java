package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.shareit.GatewayServiceClient;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final GatewayServiceClient gatewayServiceClient;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok().body(gatewayServiceClient.createUser(userRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDtoById(
            @PathVariable Long id) {
        return ResponseEntity.ok().body(gatewayServiceClient.getUserDtoById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok().body(gatewayServiceClient.updateUser(id, userRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {
        gatewayServiceClient.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
