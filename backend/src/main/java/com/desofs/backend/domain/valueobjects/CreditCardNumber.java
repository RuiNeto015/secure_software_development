package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.matchesPattern;

public class CreditCardNumber {

    private final String cardNumber;

    private CreditCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public static CreditCardNumber create(String cardNumber) {
        isTrue(cardNumber != null && !cardNumber.trim().isEmpty(),
                "Credit card number must not be null or empty.");
        matchesPattern(cardNumber,
                "^((4\\d{3})|(5[1-5]\\d{2})|(6011)|(7\\d{3}))-?\\d{4}-?\\d{4}-?\\d{4}|3[4,7]\\d{13}$", // by owasp: https://owasp.org/www-community/OWASP_Validation_Regex_Repository
                "Credit card number must start with the number 4 and be a 16-digit number.");
        return new CreditCardNumber(new String(cardNumber));
    }

    public CreditCardNumber copy() {
        return new CreditCardNumber(new String(cardNumber));
    }

    public String value() {
        return new String(cardNumber);
    }
}