package ru.practicum.shareit;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DTO.*;
import ru.practicum.enums.State;

import java.util.Collection;

@FeignClient(name = "shareIt-service", url = "${shareIt-server.url}")
public interface GatewayServiceClient {

    // BookingController
    @PostMapping("/bookings")
    BookingDto createBooking(@RequestBody BookingRequestDto bookingRequestDto,
                             @RequestHeader("X-Sharer-User-Id") Long bookerId);

    @PatchMapping("/bookings/{bookingId}")
    BookingDto updateAvailableStatusBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId);

    @PatchMapping("/bookings/cancel/{bookingId}")
    void canceledBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long bookerId);

    @GetMapping("/bookings/{bookingId}")
    BookingDto getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId);

    @GetMapping("/bookings")
    Collection<BookingDto> getAllBookingByBookerId(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") State state);

    @GetMapping("/bookings/owner")
    Collection<BookingDto> getAllBookingByOwnerId(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") State state);


    //ItemController
    @PostMapping("/items")
    ItemDto createItem(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId);

    @GetMapping("/items/{id}")
    ItemWithBookingAndCommentsDto getItemById(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long userId);

    @GetMapping("/items")
    Collection<ItemDto> searchAllItemOfOwnerById(
            @RequestHeader("X-Sharer-User-Id") Long ownerId);

    @GetMapping("/items/search")
    Collection<ItemDto> searchItemDtoByText(
            @RequestParam("text") String text);

    @PatchMapping("/items/{id}")
    ItemDto updateItem(
            @PathVariable Long id,
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long owner);

    @DeleteMapping("/items/{id}")
    void deleteItem(
            @PathVariable Long id,
            @RequestHeader("X-Sharer-User-Id") Long owner);

    @PostMapping("/items/{itemId}/comment")
    CommentDto addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CommentTextDto commentDto);


    //RequestController
    @PostMapping("/requests")
    ResponseRequestDto createRequest(
            @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId);

    @GetMapping("/requests")
    Collection<ResponseRequestDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId);

    @GetMapping("/requests/{requestId}")
    ResponseRequestDto getRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId);

    @GetMapping("/requests/all")
    Collection<ResponseRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size);


    //UserController
    @PostMapping("/users")
    UserDto createUser(
            @RequestBody UserRequestDto userRequestDto);

    @GetMapping("/users/{id}")
    UserDto getUserDtoById(
            @PathVariable Long id);

    @PatchMapping("/users/{id}")
    UserDto updateUser(
            @PathVariable Long id,
            @RequestBody UserRequestDto userRequestDto);

    @DeleteMapping("/users/{id}")
    void deleteUser(
            @PathVariable Long id);
}