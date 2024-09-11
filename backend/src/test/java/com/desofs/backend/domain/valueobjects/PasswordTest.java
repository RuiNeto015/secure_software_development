package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    private final String passwordFormatErrorMsg = "Password must contain 12 to 128 character password requiring at least 3 out 4 (uppercase and lowercase letters, numbers and special characters) and no more than 2 equal characters in a row.";

    private final String generalErrorMsg = "Password must not be null or empty.";


    @Test
    @DisplayName("Test create method with valid password")
    void testCreateValidPassword() {
        Password password = Password.create("Abcdef123!@#");
        assertNotNull(password);
        assertEquals("Abcdef123!@#", password.value());
    }

    @Test
    @DisplayName("Test create method with invalid password format")
    void testCreateInvalidPasswordFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Password.create("invalidpassword"));
        assertEquals(passwordFormatErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with null password")
    void testCreateNullPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Password.create(null));
        assertEquals(generalErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty password")
    void testCreateEmptyPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Password.create(""));
        assertEquals(generalErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with password shorter than 12 characters")
    void testCreateShortPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Password.create("Abcdefg1!"));
        assertEquals(passwordFormatErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with password longer than 128 characters")
    void testCreateLongPassword() {
        StringBuilder longPassword = new StringBuilder();
        for (int i = 0; i < 129; i++) {
            longPassword.append("a");
        }
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Password.create(longPassword.toString()));
        assertEquals(passwordFormatErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with password containing repeating characters")
    void testCreatePasswordWithRepeatingCharacters() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Password.create("Abcdef111!@#"));
        assertEquals(passwordFormatErrorMsg, exception.getMessage());
    }
}