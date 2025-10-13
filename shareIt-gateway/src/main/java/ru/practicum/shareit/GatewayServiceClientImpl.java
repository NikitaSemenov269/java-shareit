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
public class GatewayServiceClientImpl {

    private final GatewayServiceClient gatewayServiceClient;
    private final Validation validation;

    public ResponseEntity<BookingDto> createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        log.info("Запрос на создание новой бронирования от пользователя с ID: {}", bookerId);
        validation.userIdValidation(bookerId);
        validation.dateValidation(bookingRequestDto.getStart(), bookingRequestDto.getEnd());

        return ResponseEntity.ok().body(gatewayServiceClient.createBooking(bookingRequestDto, bookerId));
    }

    public ResponseEntity<BookingDto> updateAvailableStatusBooking(Long bookingId, Boolean approved, Long ownerId) {
        log.info("Запрос на обновление статуса бронирования с ID: {} от пользователя с ID: {}", bookingId, ownerId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(ownerId);

        if (approved == null) {
            throw new ValidationException("Статус не может быть null.");
        }

        return ResponseEntity.ok().body(gatewayServiceClient.updateAvailableStatusBooking(bookingId, approved, ownerId));
    }

    public ResponseEntity<Void> canceledBookingById(Long bookingId, Long bookerId) {
        log.info("Запрос на закрытие бронирования с ID: {} от пользователя с ID: {}", bookingId, bookerId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(bookerId);

        gatewayServiceClient.canceledBookingById(bookingId, bookerId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<BookingDto> getBookingById(Long bookingId, Long userId) {
        log.info("Запрос на получение  бронирования с ID: {} от пользователя с ID: {}", bookingId, userId);
        validation.bookingIdValidation(bookingId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.getBookingById(bookingId, userId));
    }

    public ResponseEntity<Collection<BookingDto>> getAllBookingByBookerId(Long bookerId, State state) {
        log.info("Запрос на получение всех бронирований пользователя с ID: {}", bookerId);
        validation.userIdValidation(bookerId);

        return ResponseEntity.ok().body(gatewayServiceClient.getAllBookingByBookerId(bookerId, state));
    }

    public ResponseEntity<Collection<BookingDto>> getAllBookingByOwnerId(Long ownerId, State state) {
        log.info("Запрос на получение всех бронирований владельца с ID: {}", ownerId);
        validation.userIdValidation(ownerId);

        return ResponseEntity.ok().body(gatewayServiceClient.getAllBookingByOwnerId(ownerId, state));
    }

    public ResponseEntity<ItemDto> createItem(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Запрос на создание предмета от пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        String available = itemRequestDto.getAvailable().trim();
        if (!"true".equals(available) && !"false".equals(available)) {
            throw new ValidationException("Значение available должно быть true / false");
        }

        return ResponseEntity.ok().body(gatewayServiceClient.createItem(itemRequestDto, userId));
    }

    public ResponseEntity<ItemWithBookingAndCommentsDto> getItemById(Long id, Long userId) {
        log.info("Запрос на получение предмета с ID: {} от пользователя с ID: {}", id, userId);
        validation.userIdValidation(userId);
        validation.itemIdValidation(id);

        return ResponseEntity.ok().body(gatewayServiceClient.getItemById(id, userId));
    }

    public ResponseEntity<Collection<ItemDto>> searchAllItemOfOwnerById(Long ownerId) {
        log.info("Запрос на получение сех предметов пользователя с ID: {}", ownerId);
        validation.userIdValidation(ownerId);

        return ResponseEntity.ok().body(gatewayServiceClient.searchAllItemOfOwnerById(ownerId));
    }

    public ResponseEntity<Collection<ItemDto>> searchItemDtoByText(String text) {
        log.info("Запрос на получение предметов по тексту: {}", text);

        if (text == null || text.isBlank()) {
            throw new ValidationException("Текст не может быть пустой строкой, или null");
        }
        return ResponseEntity.ok().body(gatewayServiceClient.searchItemDtoByText(text));
    }

    public ResponseEntity<ItemDto> updateItem(Long id, ItemRequestDto itemRequestDto, Long owner) {
        log.info("Запрос на обновление предмета с ID: {} от пользователя с ID: {}", id, owner);
        validation.userIdValidation(owner);
        validation.itemIdValidation(id);

        return ResponseEntity.ok().body(gatewayServiceClient.updateItem(id, itemRequestDto, owner));
    }

    public ResponseEntity<Void> deleteItem(Long id, Long owner) {
        log.info("Запрос на удаление предмета с ID: {} от пользователя с ID: {}", id, owner);
        validation.userIdValidation(owner);
        validation.itemIdValidation(id);

        gatewayServiceClient.deleteItem(id, owner);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<CommentDto> addComment(Long itemId, Long userId, CommentTextDto commentDto) {
        log.info("Запрос на добавление комментария от пользователя с ID: {} для предмета с ID: {}", userId, itemId);
        validation.userIdValidation(userId);
        validation.itemIdValidation(itemId);

        return ResponseEntity.ok().body(gatewayServiceClient.addComment(itemId, userId, commentDto));
    }

    public ResponseEntity<ResponseRequestDto> createRequest(RequestDto requestDto, Long userId) {
        log.info("Запрос на добавление запроса от пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.createRequest(requestDto, userId));
    }

    public ResponseEntity<Collection<ResponseRequestDto>> getUserRequests(Long userId) {
        log.info("Запрос на получение запросов пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.getUserRequests(userId));
    }

    public ResponseEntity<ResponseRequestDto> getRequestById(Long requestId, Long userId) {
        log.info("Запрос на получение запроса с ID: {} от пользователя с ID: {}", userId, requestId);
        validation.userIdValidation(userId);
        validation.requestIdValidation(requestId);

        return ResponseEntity.ok().body(gatewayServiceClient.getRequestById(requestId, userId));
    }

    public ResponseEntity<Collection<ResponseRequestDto>> getOtherUserRequests(Long userId, Integer from, Integer size) {
        log.info("Запрос на получение всех запросов кроме пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

        return ResponseEntity.ok().body(gatewayServiceClient.getAllRequests(userId, from, size));
    }

    public ResponseEntity<UserDto> createUser(UserRequestDto userRequestDto) {
        log.info("Запрос на создание нового пользователя.");

        validation.userEmailValidation(userRequestDto.getEmail());
        validation.userNameValidation(userRequestDto.getName());

        return ResponseEntity.ok().body(gatewayServiceClient.createUser(userRequestDto));
    }

    public ResponseEntity<UserDto> getUserDtoById(Long id) {
        log.info("Запрос на получение данных пользователя с ID: {}", id);
        validation.userIdValidation(id);

        return ResponseEntity.ok().body(gatewayServiceClient.getUserDtoById(id));
    }

    public ResponseEntity<UserDto> updateUser(Long id, UserRequestDto userRequestDto) {
        log.info("Запрос на обновление данных пользователя с ID: {}", id);
        validation.userIdValidation(id);

        return ResponseEntity.ok().body(gatewayServiceClient.updateUser(id, userRequestDto));
    }

    public ResponseEntity<Void> deleteUser(Long id) {
        log.info("Запрос на удаление пользователя с ID: {}", id);
        validation.userIdValidation(id);

        gatewayServiceClient.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
