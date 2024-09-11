package com.desofs.backend.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity(name = "image")
@Getter
public class ImageUrlDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String reviewId;

    @Column(nullable = false)
    private String reference;

    public ImageUrlDB() {
    }

    public ImageUrlDB(String id, String reviewId, String reference) {
        this.id = id;
        this.reviewId = reviewId;
        this.reference = reference;
    }
}
