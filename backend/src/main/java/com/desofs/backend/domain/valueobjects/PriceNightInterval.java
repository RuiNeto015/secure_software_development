package com.desofs.backend.domain.valueobjects;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class PriceNightInterval {

    private final Id id;
    private final Id rentalPropertyId;
    private final MoneyAmount price;
    private final IntervalTime interval;

    public PriceNightInterval(Id id, Id rentalPropertyId, MoneyAmount price, IntervalTime interval) {
        this.id = id.copy();
        this.rentalPropertyId = rentalPropertyId.copy();
        this.price = price.copy();
        this.interval = interval.copy();
    }

    public static PriceNightInterval create(Id id, Id rentalPropertyId, MoneyAmount price, IntervalTime interval) {
        notNull(id, "Id must not be null.");
        notNull(rentalPropertyId, "Rental Property Id must not be null.");
        notNull(price, "Price must not be null.");
        notNull(interval, "Interval must not be null.");

        return new PriceNightInterval(id, rentalPropertyId, price, interval);
    }

    public PriceNightInterval(Id rentalPropertyId, MoneyAmount price, IntervalTime interval) {
        this(Id.create(UUID.randomUUID().toString()), rentalPropertyId, price, interval);
    }

    public Id getId() {
        return id.copy();
    }

    public Id getRentalPropertyId() {
        return rentalPropertyId.copy();
    }

    public MoneyAmount getPrice() {
        return price.copy();
    }

    public IntervalTime getInterval() {
        return interval.copy();
    }
}