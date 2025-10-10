package ru.practicum.shareit.exception;

public class DataIntegrityException extends RuntimeException {
    public DataIntegrityException(String message) {
        super(message);
    }
}
