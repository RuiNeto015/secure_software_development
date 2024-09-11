package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyDescriptionTest {


    @Test
    @DisplayName("Test create method with valid description")
    void testCreateValidDescription() {
        PropertyDescription propertyDescription = PropertyDescription.create("This is a valid description.");
        assertNotNull(propertyDescription);
        assertEquals("This is a valid description.", propertyDescription.value());
    }

    @Test
    @DisplayName("Test create method with null description")
    void testCreateNullDescription() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> PropertyDescription.create(null));
        assertEquals("Property description must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty description")
    void testCreateEmptyDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PropertyDescription.create(""));
        assertEquals("Property description must not be empty.", exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with description containing only whitespaces")
    void testCreateWhitespaceDescription() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> PropertyDescription.create("   "));
        assertEquals("Property description must not be empty.", exception.getMessage());
    }
}