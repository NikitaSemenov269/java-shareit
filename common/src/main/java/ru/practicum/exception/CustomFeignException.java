package ru.practicum.exception;

import lombok.Getter;

@Getter
public class CustomFeignException extends RuntimeException {
    private final int status;
    private final String responseBody;

    public CustomFeignException(String message, int status, String responseBody) {
        super(message);
        this.status = status;
        this.responseBody = responseBody;
    }
}