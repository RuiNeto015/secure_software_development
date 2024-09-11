package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewStarsTest {

    final String generalErrorMsg = "Stars must be between 1 and 5 inclusive.";

    @Test
    @DisplayName("Test create method with valid number of stars")
    void testCreateValidReviewStars() {
        ReviewStars reviewStars = ReviewStars.create(3);
        assertNotNull(reviewStars);
        assertEquals(3, reviewStars.getStars());
    }

    @Test
    @DisplayName("Test create method with minimum stars")
    void testCreateMinimumReviewStars() {
        ReviewStars reviewStars = ReviewStars.create(1);
        assertNotNull(reviewStars);
        assertEquals(1, reviewStars.getStars());
    }

    @Test
    @DisplayName("Test create method with maximum stars")
    void testCreateMaximumReviewStars() {
        ReviewStars reviewStars = ReviewStars.create(5);
        assertNotNull(reviewStars);
        assertEquals(5, reviewStars.getStars());
    }

    @Test
    @DisplayName("Test create method with less than minimum stars")
    void testCreateLessThanMinimumReviewStars() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ReviewStars.create(0));
        assertEquals(generalErrorMsg, exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with more than maximum stars")
    void testCreateMoreThanMaximumReviewStars() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> ReviewStars.create(6));
        assertEquals(generalErrorMsg, exception.getMessage());
    }
}