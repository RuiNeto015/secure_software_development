package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CreditCardNumberTest {

    @Test
    @DisplayName("Test create method with valid credit card number 1")
    void testCreateValidCreditCardNumber() {
        CreditCardNumber ccn = CreditCardNumber.create("4111111111111111");
        assertNotNull(ccn);
        assertEquals("4111111111111111", ccn.value());
    }

    @Test
    @DisplayName("Test create method with valid credit card number 2")
    void testCreateInvalidCreditCardNumberFormat() {
        CreditCardNumber ccn = CreditCardNumber.create("4111-1111-1111-1111");
        assertEquals("4111-1111-1111-1111", ccn.value());
    }

    @Test
    @DisplayName("Test create method with null credit card number")
    void testCreateNullCreditCardNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardNumber.create(null));
        assertEquals("Credit card number must not be null or empty.", exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty credit card number")
    void testCreateEmptyCreditCardNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> CreditCardNumber.create(""));
        assertEquals("Credit card number must not be null or empty.", exception.getMessage());
    }
}