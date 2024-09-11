package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    final String generalErrorMsg = "Email must not be null or empty.";

    @Test
    @DisplayName("Test create method with valid email")
    void testCreateValidEmail() {
        Email email = Email.create("test@example.com");
        assertNotNull(email);
        assertEquals("test@example.com", email.value());
    }

    @Test
    @DisplayName("Test create method with invalid email format")
    void testCreateInvalidEmailFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Email.create("invalid_email_format"));
        assertEquals("Invalid email format.", exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with null email")
    void testCreateNullEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Email.create(null));
        assertEquals(generalErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty email")
    void testCreateEmptyEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Email.create(""));
        assertEquals(generalErrorMsg, exception.getMessage());
    }
}