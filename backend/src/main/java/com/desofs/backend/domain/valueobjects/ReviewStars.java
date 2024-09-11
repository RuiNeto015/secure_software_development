package com.desofs.backend.domain.valueobjects;

import lombok.Getter;

import static org.apache.commons.lang3.Validate.isTrue;

@Getter
public class ReviewStars {

    private final int stars;

    private ReviewStars(int stars) {
        this.stars = stars;
    }

    public static ReviewStars create(int stars) {
        isTrue(stars >= 1 && stars <= 5,
                "Stars must be between 1 and 5 inclusive.");
        return new ReviewStars(stars);
    }

    public ReviewStars copy() {
        return new ReviewStars(stars);
    }

    public int value() {
        return this.stars;
    }

}