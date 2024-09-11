package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class PropertyName {

    private final String name;

    private PropertyName(String name) {
        this.name = name;
    }

    public static PropertyName create(String name) {
        isTrue(name != null && !name.trim().isEmpty(),
                "Property name must not be null or empty.");
        matchesPattern(name,
                "^[a-zA-Z0-9 ]+$",
                "Property name must contain only letters and digits.");
        return new PropertyName(new String(name));
    }

    public PropertyName copy() {
        return new PropertyName(new String(name));
    }

    public String value() {
        return new String(name);
    }
}