package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.notNull;
import static org.springframework.util.Assert.isTrue;

public class PropertyDescription {

    private final String description;

    private PropertyDescription(String description) {
        this.description = description;
    }

    public static PropertyDescription create(String description) {
        notNull(description,
                "Property description must not be null.");
        isTrue(!description.trim().isEmpty(),
                "Property description must not be empty.");
        return new PropertyDescription(new String(description));
    }

    public PropertyDescription copy() {
        return new PropertyDescription(new String(description));
    }

    public String value() {
        return new String(description);
    }
}