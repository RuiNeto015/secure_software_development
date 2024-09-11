package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTextTest {

    private final String reviewTextErrorMessage = "Review text must not be null or empty.";
    @Test
    @DisplayName("Test create method with valid review text")
    void testCreateValidReviewText() {
        ReviewText reviewText = ReviewText.create("This is a valid review text.");
        assertNotNull(reviewText);
        assertEquals("This is a valid review text.", reviewText.value());
    }

    @Test
    @DisplayName("Test create method with null review text")
    void testCreateNullReviewText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ReviewText.create(null));
        assertEquals(reviewTextErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with empty review text")
    void testCreateEmptyReviewText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ReviewText.create(""));
        assertEquals(reviewTextErrorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with whitespace review text")
    void testCreateWhitespaceReviewText() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ReviewText.create("   "));
        assertEquals(reviewTextErrorMessage, exception.getMessage());
    }
}