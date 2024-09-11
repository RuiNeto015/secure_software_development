package com.desofs.backend.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Entity(name = "price_night_interval")
@Getter
public class PriceNightIntervalDB {

    @Id
    private String id;

    @Column(nullable = false)
    private String rentalPropertyId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Date fromDate;

    @Column(nullable = false)
    private Date toDate;

    public PriceNightIntervalDB() {
    }

    public PriceNightIntervalDB(String id, String rentalPropertyId, BigDecimal price, Date from, Date to) {
        this.id = id;
        this.rentalPropertyId = rentalPropertyId;
        this.price = price;
        this.fromDate = from;
        this.toDate = to;
    }
}
