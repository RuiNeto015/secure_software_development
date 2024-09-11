package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class Id {

    private final String id;

    private Id(String id) {
        this.id = id;
    }

    public static Id create(String id) {
        isTrue(id != null || !id.trim().isEmpty(),
                "ID must not be null or empty.");
        matchesPattern(id,
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                "ID must be an UUID.");
        return new Id(new String(id));
    }

    public Id copy() {
        return new Id(new String(id));
    }

    public String value() {
        return new String(id);
    }
}