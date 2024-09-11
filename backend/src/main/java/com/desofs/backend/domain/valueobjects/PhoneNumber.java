package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class PhoneNumber {

    private final String phoneNumber;

    private PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // BEGIN-NOSCAN
    public static PhoneNumber create(String phoneNumber) {
        isTrue(phoneNumber != null || !phoneNumber.trim().isEmpty(),
                "Phone number must not be null or empty.");
        matchesPattern(phoneNumber,
                "^(2\\d{8}|9\\d{8})$",
                "Phone number does not match the pattern.");
        return new PhoneNumber(new String(phoneNumber));
    }
    // END-NOSCAN

    public PhoneNumber copy() {
        return new PhoneNumber(new String(phoneNumber));
    }

    public String value() {
        return new String(phoneNumber);
    }
}
