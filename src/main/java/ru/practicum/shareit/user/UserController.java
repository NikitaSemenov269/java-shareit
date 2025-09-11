package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public UserDTO getUserDTOById(@PathVariable @Min(1) Long id) {
        return userService.getUserDTOById(id);
    }

    @PatchMapping
    public User updateUser(@Valid
                           @PathVariable Long userId,
                           @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Min(1) Long userId) {
        userService.deleteUser(userId);
    }
}
