package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class CardVerificationCode {

    private final String verificationCode;

    private CardVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public static CardVerificationCode create(String verificationCode) {
        isTrue(verificationCode != null && !verificationCode.trim().isEmpty(),
                "Verification code must not be null or empty.");
        matchesPattern(verificationCode,
                "^[0-9]{3}$",
                "Verification code must be a 3 or 4-digit number.");
        return new CardVerificationCode(new String(verificationCode));
    }

    public CardVerificationCode copy() {
        return new CardVerificationCode(new String(verificationCode));
    }

    public String value() {
        return new String(verificationCode);
    }
}