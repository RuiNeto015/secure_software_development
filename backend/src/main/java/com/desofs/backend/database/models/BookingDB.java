package com.desofs.backend.database.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(name = "booking")
@Getter
public class BookingDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String propertyId;

    @Column(nullable = false)
    private Date fromDate;

    @Column(nullable = false)
    private Date toDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "booking", cascade = CascadeType.ALL)
    private List<EventDB> events;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public BookingDB() {
    }

    public BookingDB(String id, String accountId, String propertyId, Date from, Date to, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.propertyId = propertyId;
        this.fromDate = from;
        this.toDate = to;
        this.createdAt = createdAt;
    }
}
