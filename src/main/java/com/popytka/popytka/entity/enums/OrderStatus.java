package com.popytka.popytka.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    NEW("Новая"),
    PROCESSING("В обработке"),
    CONFIRMED("Подтверждена"),
    REJECTED("Отклонена"),
    PAID("Оплачена"),
    CANCELLED("Отменена");

    private final String displayName;

    public static OrderStatus fromDisplayName(String name) {
        for (OrderStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестный статус: " + name);
    }
}
