package ru.practicum.shareit.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {

    AWAITING_A_REQUEST("Номинальный статус бронирования", true),
    WAITING("Новое бронирование, ожидает одобрения", false),
    APPROVED("Бронирование подтверждено владельцем", true),
    REJECTED("Бронирование отклонено владельцем", false),
    CANCELED("Бронирование отменено создателем", false);

    private final String description;
    private final boolean status;

    BookingStatus(String description, boolean status) {
        this.description = description;
        this.status = status;
    }
}
