package ru.practicum.shareit.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {

    AWAITING_A_REQUEST("Номинальный статус бронирования", true),
    WAITING("Новое бронирование, ожидает одобрения", true),
    APPROVED("Бронирование подтверждено владельцем", false),
    REJECTED("Бронирование отклонено владельцем", true),
    CANCELED("Бронирование отменено создателем", true);

    private final String description;
    private final boolean status;

    BookingStatus(String description, boolean status) {
        this.description = description;
        this.status = status;
    }
}
