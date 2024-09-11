package com.desofs.backend.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class IntervalTimeTest {

    @Test
    public void testIntervalTimeCreation() {
        Date fromDate = new Date(1000); // 1 second after epoch
        Date toDate = new Date(2000);   // 2 seconds after epoch
        IntervalTime intervalTime = IntervalTime.create(fromDate, toDate);

        assertEquals(fromDate, intervalTime.getFrom());
        assertEquals(toDate, intervalTime.getTo());
    }

    @Test
    public void testNullFromDate() {
        Date toDate = new Date(2000);
        assertThrows(NullPointerException.class, () -> IntervalTime.create(null, toDate));
    }

    @Test
    public void testNullToDate() {
        Date fromDate = new Date(1000);
        assertThrows(NullPointerException.class, () -> IntervalTime.create(fromDate, null));
    }

    @Test
    public void testImmutableFromDate() {
        Date fromDate = new Date(1000); // 1 second after epoch
        Date toDate = new Date(2000);   // 2 seconds after epoch
        IntervalTime intervalTime = IntervalTime.create(fromDate, toDate);

        // Modify the original fromDate after creating the intervalTime
        fromDate.setTime(500);

        // Check if the fromDate of the intervalTime remains unchanged
        assertEquals(new Date(1000), intervalTime.getFrom());
    }

    @Test
    public void testImmutableToDate() {
        Date fromDate = new Date(1000); // 1 second after epoch
        Date toDate = new Date(2000);   // 2 seconds after epoch
        IntervalTime intervalTime = IntervalTime.create(fromDate, toDate);

        // Modify the original toDate after creating the intervalTime
        toDate.setTime(3000);

        // Check if the toDate of the intervalTime remains unchanged
        assertEquals(new Date(2000), intervalTime.getTo());
    }
}