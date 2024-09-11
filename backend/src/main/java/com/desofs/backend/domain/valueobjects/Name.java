package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.matchesPattern;
import static org.springframework.util.Assert.isTrue;

public class Name {

    private final String name;

    private Name(String name) {
        this.name = name;
    }

    public static Name create(String name) {
        isTrue(name != null && !name.trim().isEmpty(),
                "Name must not be null or empty.");
        matchesPattern(name,
                "^[\\p{L}]+((['., -][\\p{L} ])?[\\p{L}]*)*$", // by owasp: https://owasp.org/www-community/OWASP_Validation_Regex_Repository
                "Name must contain only letters, spaces, hyphens, and apostrophes.");
        return new Name(new String(name));
    }

    public Name copy() {
        return new Name(new String(name));
    }

    public String value() {
        return new String(name);
    }

}
