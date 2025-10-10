package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.DTO.*;
import ru.practicum.enums.State;
import ru.practicum.exception.ValidationException;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayServiceImpl {

    private final GatewayServiceClient gatewayServiceClient;
    private final Validation validation;

    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        log.info("Запрос на добавление новой брони от пользователя с ID: {}", bookerId);
        validation.userIdValidation(bookerId);

        return gatewayServiceClient.createBooking(bookingRequestDto, bookerId);
    }

    public BookingDto updateAvailableStatusBooking(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Запрос на обновление статуса брони ID: {} от пользователя с ID: {}", bookingId, ownerId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(ownerId);

        if (approved == null) {
            throw new ValidationException("Статус не может быть null.");
        }

        return gatewayServiceClient.updateAvailableStatusBooking(bookingId, approved, ownerId);
    }


    public void canceledBookingById(Long bookingId, Long bookerId) {
        log.info("Запрос на закрытие брони ID: {} от пользователя с ID: {}", bookingId, bookerId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(bookerId);

        gatewayServiceClient.canceledBookingById(bookingId, bookerId);
    }


    public ResponseEntity<BookingDto> getBookingById(Long bookingId, Long userId) {
        return null;
    }


    public ResponseEntity<Collection<BookingDto>> getAllBookingByBookerId(Long bookerId, State state) {
        return null;
    }


    public ResponseEntity<Collection<BookingDto>> getAllBookingByOwnerId(Long ownerId, State state) {
        return null;
    }


    public ResponseEntity<ItemDto> createItem(ItemRequestDto itemRequestDto, Long userId) {
        return null;
    }


    public ResponseEntity<ItemWithBookingAndCommentsDto> getItemById(Long id, Long userId) {
        return null;
    }


    public ResponseEntity<Collection<ItemDto>> searchAllItemOfOwnerById(Long ownerId) {
        return null;
    }


    public ResponseEntity<Collection<ItemDto>> searchItemDTOByText(String text) {
        return null;
    }


    public ResponseEntity<ItemDto> updateItem(Long id, ItemRequestDto itemRequestDto, Long owner) {
        return null;
    }


    public ResponseEntity<Void> deleteItem(Long id, Long owner) {
        return null;
    }

    public ResponseEntity<CommentDto> addComment(Long itemId, Long userId, CommentTextDto commentDto) {
        return null;
    }

    public ResponseEntity<ResponseRequestDto> createRequest(RequestDto requestDto, Long userId) {
        return null;
    }

    public ResponseEntity<Collection<ResponseRequestDto>> getUserRequests(Long userId) {
        return null;
    }

    public ResponseEntity<ResponseRequestDto> getRequestById(Long requestId, Long userId) {
        return null;
    }

    public ResponseEntity<Collection<ResponseRequestDto>> getAllRequests(Long userId, Integer from, Integer size) {
        return null;
    }

    public ResponseEntity<UserDto> createUser(UserRequestDto userRequestDto) {
        return null;
    }

    public ResponseEntity<UserDto> getUserDtoById(Long id) {
        return null;
    }

    public ResponseEntity<UserDto> updateUser(Long id, UserRequestDto userRequestDto) {
        return null;
    }

    public ResponseEntity<Void> deleteUser(Long id) {
        return null;
    }
}
