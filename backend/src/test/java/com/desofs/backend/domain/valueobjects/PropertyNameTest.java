package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyNameTest {

    private final String propertyNameErrorMessage = "Property name must not be null or empty.";

    @Test
    @DisplayName("Test create method with valid property name")
    void testCreateValidPropertyName() {
        PropertyName propertyName = PropertyName.create("PropertyName123");
        assertNotNull(propertyName);
        assertEquals("PropertyName123", propertyName.value());
    }

    @Test
    @DisplayName("Test create method with null property name")
    void testCreateNullPropertyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PropertyName.create(null));
        assertEquals(propertyNameErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty property name")
    void testCreateEmptyPropertyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PropertyName.create(""));
        assertEquals(propertyNameErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with whitespace property name")
    void testCreateWhitespacePropertyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PropertyName.create("   "));
        assertEquals(propertyNameErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with invalid characters in property name")
    void testCreateInvalidCharactersPropertyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PropertyName.create("PropertyName@123"));
        assertEquals("Property name must contain only letters and digits.", exception.getMessage());
    }
}