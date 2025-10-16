package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.DTO.*;
import ru.practicum.exception.ValidationException;
import ru.practicum.shareit.GatewayServiceClient;
import ru.practicum.shareit.Validation;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayItem {

    private final Validation validation;
    private final GatewayServiceClient gatewayServiceClient;

    public ResponseEntity<ItemDto> createItem(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Запрос на создание предмета от пользователя с ID: {}", userId);
        validation.userIdValidation(userId);

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
}
