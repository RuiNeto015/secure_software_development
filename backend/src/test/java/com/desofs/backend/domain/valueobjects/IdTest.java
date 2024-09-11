package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @Test
    public void testIdCreation() {
        assertThrows(IllegalArgumentException.class, () -> Id.create("test_id_123"));
    }

    @Test
    public void testNullId() {
        assertThrows(NullPointerException.class, () -> Id.create(null));
    }

    @Test
    public void testEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> Id.create(""));
    }

    @Test
    public void testWhitespaceId() {
        assertThrows(IllegalArgumentException.class, () -> Id.create("   "));
    }

    @Test
    public void testNonAlphanumericId() {
        assertThrows(IllegalArgumentException.class, () -> Id.create("test id"));
    }

    @Test
    public void testIdWithSymbols() {
        assertThrows(IllegalArgumentException.class, () -> Id.create("test_id!"));
    }

    @Test
    public void testIdWithWhitespace() {
        assertThrows(IllegalArgumentException.class, () -> Id.create("test_id 123"));
    }
}