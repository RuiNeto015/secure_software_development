package com.desofs.backend.database.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity(name = "event")
@Getter
public class EventDB {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingDB booking;

    @Email
    @Column(nullable = false, unique = true)
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private StateDB state;


    public EventDB() {
    }

    public EventDB(String id, BookingDB booking, LocalDateTime dateTime, StateDB state) {
        this.id = id;
        this.booking = booking;
        this.dateTime = dateTime;
        this.state = state;
    }
}