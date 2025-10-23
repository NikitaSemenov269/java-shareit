package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.DTO.UserDto;
import ru.practicum.DTO.UserRequestDto;
import ru.practicum.shareit.GatewayServiceClient;
import ru.practicum.shareit.Validation;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayUser {

    private final Validation validation;
    private final GatewayServiceClient gatewayServiceClient;

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
