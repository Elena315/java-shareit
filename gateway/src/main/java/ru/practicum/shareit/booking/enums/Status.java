package ru.practicum.shareit.booking.enums;

import java.util.Optional;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED,
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    UNSUPPORTED_STATUS;

    public static Optional<Status> from(String stringState) {
        for (Status state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}