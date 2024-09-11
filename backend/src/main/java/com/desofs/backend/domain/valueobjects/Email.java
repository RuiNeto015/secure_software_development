package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class Email {

    private final String email;

    private Email(String email) {
        this.email = email;
    }

    public static Email create(String email) {
        isTrue(email != null && !email.trim().isEmpty(),
                "Email must not be null or empty.");
        matchesPattern(email,
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$", // by owasp: https://owasp.org/www-community/OWASP_Validation_Regex_Repository
                "Invalid email format.");
        return new Email(new String(email));
    }

    public Email copy() {
        return new Email(new String(email));
    }

    public String value() {
        return new String(email);
    }
}