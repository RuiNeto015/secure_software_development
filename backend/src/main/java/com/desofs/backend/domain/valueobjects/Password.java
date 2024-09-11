package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class Password {

    private final String password;

    private Password(String password) {
        this.password = password;
    }

    public static Password create(String password) {
        isTrue(password != null && !password.trim().isEmpty(),
                "Password must not be null or empty.");
        matchesPattern(password,
                "^(?:(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))(?!.*(.)\\1{2,})[A-Za-z0-9!~<>,;:_=?*+#.\"&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{12,128}$", // by owasp: https://owasp.org/www-community/OWASP_Validation_Regex_Repository
                "Password must contain 12 to 128 character password requiring at least 3 out 4 (uppercase and lowercase letters, numbers and special characters) and no more than 2 equal characters in a row.");
        return new Password(password);
    }

    public Password copy() {
        return new Password(password);
    }

    public String value() {
        return password;
    }
}