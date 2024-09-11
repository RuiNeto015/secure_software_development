package com.desofs.backend.domain.valueobjects;

import static org.apache.commons.lang3.Validate.isTrue;

public class ReviewText {

    private final String text;

    private ReviewText(String text) {
        this.text = text;
    }

    public static ReviewText create(String text) {
        isTrue(text != null && !text.trim().isEmpty(),
                "Review text must not be null or empty.");
        return new ReviewText(new String(text));
    }

    public ReviewText copy() {
        return new ReviewText(new String(text));
    }

    public String value() {
        return new String(text);
    }
}
