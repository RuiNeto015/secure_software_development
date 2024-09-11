package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonalNameTest {

    final String generalErrorMsg = "Name must not be null or empty.";

    @Test
    @DisplayName("Test create method with valid name")
    void testCreateValidName() {
        Name personalName = Name.create("John Doe");
        assertNotNull(personalName);
        assertEquals("John Doe", personalName.value());
    }

    @Test
    @DisplayName("Test create method with invalid name format")
    void testCreateInvalidNameFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Name.create("John123"));
        assertEquals("Name must contain only letters, spaces, hyphens, and apostrophes.", exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with null name")
    void testCreateNullName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Name.create(null));
        assertEquals(generalErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty name")
    void testCreateEmptyName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Name.create(""));
        assertEquals(generalErrorMsg, exception.getMessage());
    }
}