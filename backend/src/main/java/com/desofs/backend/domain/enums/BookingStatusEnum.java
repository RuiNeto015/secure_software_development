package com.desofs.backend.domain.enums;

import lombok.Getter;

@Getter
public enum BookingStatusEnum {
    BOOKED(1),
    COMPLETED(2),
    CANCELLED(3),
    REFUNDED(4);

    private final int id;

    BookingStatusEnum(int id) {
        this.id = id;
    }

    public static BookingStatusEnum fromId(int id) {
        for (BookingStatusEnum status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown id: " + id);
    }
}