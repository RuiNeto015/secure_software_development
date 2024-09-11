package com.desofs.backend.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity(name = "review")
@Getter
public class ReviewDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false)
    private String state;

    public ReviewDB() {
    }

    public ReviewDB(String id, String userId, String bookingId, String text, Integer stars, String state) {
        this.id = id;
        this.userId = userId;
        this.bookingId = bookingId;
        this.text = text;
        this.stars = stars;
        this.state = state;
    }
}
