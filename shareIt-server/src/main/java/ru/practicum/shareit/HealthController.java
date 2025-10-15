package ru.practicum.shareit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Server is running on port 9090";
    }

    @GetMapping("/bookings/test")
    public String testBookings() {
        return "Bookings controller is working!";
    }
}