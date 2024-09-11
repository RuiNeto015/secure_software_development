package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositiveIntegerTest {

    @Test
    @DisplayName("Test create method with valid positive integer")
    void testCreateValidPositiveInteger() {
        PositiveInteger positiveInteger = PositiveInteger.create(10);
        assertNotNull(positiveInteger);
        assertEquals(10, positiveInteger.value());
    }

}