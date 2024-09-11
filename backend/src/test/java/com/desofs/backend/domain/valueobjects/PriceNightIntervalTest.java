package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PriceNightIntervalTest {

    @Test
    @DisplayName("Test create method with valid price and interval")
    void testCreateValidPriceNightInterval() {
        MoneyAmount price = MoneyAmount.create(BigDecimal.valueOf(50));
        Date startDate = new Date(2024, 5, 1);
        Date endDate = new Date(2024, 5, 2);
        IntervalTime interval = IntervalTime.create(startDate, endDate);
        PriceNightInterval priceNightInterval = PriceNightInterval.create(Id.create(UUID.randomUUID().toString()),
                Id.create(UUID.randomUUID().toString()), price, interval);
        assertNotNull(priceNightInterval);
        assertEquals(price.value(), priceNightInterval.getPrice().value());
        assertEquals(interval.getTo(), priceNightInterval.getInterval().getTo());
        assertEquals(interval.getFrom(), priceNightInterval.getInterval().getFrom());
    }

    @Test
    @DisplayName("Test create method with null price")
    void testCreateNullPrice() {
        Date startDate = new Date(2024, 5, 1);
        Date endDate = new Date(2024, 5, 2);
        IntervalTime interval = IntervalTime.create(startDate, endDate);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> PriceNightInterval.create(null, null, null, interval));
        assertEquals("Id must not be null.", exception.getMessage());
    }

    @Test
    @DisplayName("Test create method with null interval")
    void testCreateNullInterval() {
        MoneyAmount price = MoneyAmount.create(BigDecimal.valueOf(50));
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> PriceNightInterval.create(Id.create(UUID.randomUUID().toString()),
                        Id.create(UUID.randomUUID().toString()), price, null));
        assertEquals("Interval must not be null.", exception.getMessage());
    }

}