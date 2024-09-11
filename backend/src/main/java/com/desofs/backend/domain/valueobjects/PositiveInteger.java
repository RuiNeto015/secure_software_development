package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;

public class PositiveInteger {

    private final int value;

    private PositiveInteger(int value) {
        this.value = value;
    }

    public static PositiveInteger create(int value) {
        isTrue(value >= 0,
                "Value must be a positive integer.");
        return new PositiveInteger(value);
    }

    public PositiveInteger copy() {
        return new PositiveInteger(value);
    }

    public int value() {
        return value;
    }
}
