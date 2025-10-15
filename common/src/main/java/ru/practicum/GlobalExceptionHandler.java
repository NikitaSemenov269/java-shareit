package ru.practicum;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.CustomFeignException;
import ru.practicum.exception.NotFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream().map(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                return fieldError.getField() + ": " + fieldError.getDefaultMessage();
            }
            return error.getDefaultMessage();
        }).collect(Collectors.joining("; "));

        log.error("Ошибка валидации: {}", errorMessage);
        return new ErrorResponse(errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Объект не найден: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    // ДОБАВЬТЕ ЭТОТ МЕТОД ДЛЯ ОБРАБОТКИ FEIGN ИСКЛЮЧЕНИЙ
    @ExceptionHandler
    public ErrorResponse handleCustomFeignException(final CustomFeignException e) {
        HttpStatus status = mapToHttpStatus(e.getStatus());

        // Логируем в зависимости от типа ошибки
        if (e.getStatus() >= 500) {
            log.error("Ошибка микросервиса {}: {}", e.getStatus(), e.getMessage());
        } else {
            log.warn("Ошибка клиента {}: {}", e.getStatus(), e.getMessage());
        }

        // Создаем ErrorResponse с сообщением из микросервиса
        String errorMessage = extractErrorMessage(e.getResponseBody(), status);
        ErrorResponse errorResponse = new ErrorResponse(errorMessage);

        // Устанавливаем статус ответа
        org.springframework.web.context.request.RequestContextHolder
                .getRequestAttributes()
                .setAttribute("org.springframework.web.servlet.HandlerMapping.redirectHttpStatus",
                        status, 0);

        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(final Exception e) {
        log.error("Ошибка сервера: {}", e.getMessage(), e);
        return new ErrorResponse("Внутренняя ошибка сервера.");
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ FEIGN ОБРАБОТКИ

    private HttpStatus mapToHttpStatus(int feignStatus) {
        return switch (feignStatus) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> {
                if (feignStatus >= 400 && feignStatus < 500) {
                    yield HttpStatus.BAD_REQUEST;
                } else {
                    yield HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }
        };
    }

    private String extractErrorMessage(String responseBody, HttpStatus httpStatus) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return httpStatus.getReasonPhrase();
        }

        try {
            // Пытаемся распарсить JSON и извлечь сообщение об ошибке
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(responseBody);

            if (node.has("error")) return node.get("error").asText();
            if (node.has("message")) return node.get("message").asText();
            if (node.has("description")) return node.get("description").asText();

            return "Ошибка микросервиса: " + responseBody;

        } catch (Exception e) {
            // Если не JSON, возвращаем общее сообщение
            return "Ошибка микросервиса: " + httpStatus.getReasonPhrase();
        }
    }
}