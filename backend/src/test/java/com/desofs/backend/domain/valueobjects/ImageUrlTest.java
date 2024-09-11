package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImageUrlTest {

    final String validUrl = "https://example.com/image.jpg";

    @Test
    @DisplayName("Test valid URL")
    void testValidUrl() {
        ImageUrl imageUrl = ImageUrl.create(Id.create(UUID.randomUUID().toString()), validUrl);
        assertEquals(validUrl, imageUrl.getUrl());
    }

    @Test
    @DisplayName("Test null URL")
    void testNullUrl() {
        assertThrows(NullPointerException.class, () -> ImageUrl.create(null, null));
    }

    @Test
    @DisplayName("Test empty URL")
    void testEmptyUrl() {
        assertThrows(IllegalArgumentException.class, () -> ImageUrl.create(Id.create(UUID.randomUUID().toString()), ""));
    }
}
